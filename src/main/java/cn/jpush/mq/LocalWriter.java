package cn.jpush.mq;

import cn.jpush.tool.Alarm;
import cn.jpush.tool.CommandWithAlarm;
import cn.jpush.tool.Time;
import cn.jpush.tool.UploadToHdfs;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public abstract class LocalWriter {
    protected static final Logger LOG = Logger.getLogger(LocalWriter.class);
    public static final String TEMP = ".tmp.";
    public static final String UNKNOWN = "unknown";

    private int ttl = 3;

    private Address add;
    private String mqName;
    private String localPath;
    private String hdfsPath;

    public LocalWriter(Address address, String qname, String pathL, String pathH) {
        add = address;
        mqName = qname;
        localPath = pathL;
        hdfsPath = pathH;

        if (new File(localPath).mkdirs()) {
            LOG.info("create the Dir " + localPath);
        }
    }

    public void setTTL(int days) {
        ttl = days;
    }

    public String getLocalPath(String file) {
        return localPath + File.separator + add.getHost() + TEMP + file;
    }

    public String getFileDead(String timeTag) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(Time.getTimeStamp(timeTag));
        cal.add(Calendar.DAY_OF_MONTH, -ttl);
        return Time.format("yyyymmddhh", cal.getTimeInMillis());
    }

    public UploadToHdfs getUploadCmd(String timeTag, String currHdfsPath) {
        String dir = localPath + File.separator;
        String fileMerged = dir + "*" + TEMP + timeTag;
        String fileFinal = dir + timeTag;

        // clean file over ttl
        String fileDead = dir + getFileDead(timeTag);
        File dead = new File(fileDead);
        if (dead.isFile()) {
            dead.delete();
        }

        String mergeCmd = "cat " + fileMerged + ">" + fileFinal;
        String cleanCmd = "rm " + fileMerged;
        String preCmd = mergeCmd + " && " + cleanCmd;

        return UploadToHdfs.newInstance(new CommandWithAlarm(Alarm.FAIL_CMD, preCmd), fileFinal,
                currHdfsPath);
    }

    public int run(boolean merge) {
        String time = Time.format("yyyymmddhh", System.currentTimeMillis());
        String day = time.substring(0, 8);
        String file = getLocalPath(time);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            LOG.error(file + " not found");
            return -1;
        }

        MQ mq = MQFactory.newInstance(mqName);
        QueueingConsumer consumer = mq.createConsumer(add);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        String currTime = null;
        String record = null;
        Delivery delivery = null;
        String currDay = null;
        String currHdfsPath = null;

        String initTimeInfix = time.substring(0, 4) + File.separator + time.substring(4, 6) + File.separator + time.substring(6, 8);
        // 修改上传的hdfs的路径格式为/user/log/hive/.../{year}/{month}/{day}/yyyymmddhh
        String initHdfsPath = hdfsPath + initTimeInfix;
        LOG.info("initial upload hdfs path: " + initHdfsPath);
        currHdfsPath = initHdfsPath;

        while (true) {
            currTime = Time.format("yyyymmddhh", System.currentTimeMillis());
            if (!currTime.equals(time)) {
                if (null != writer) {
                    LOG.info("close file:" + file);
                    writer.close();
                    String timeInfix = time.substring(0, 4) + File.separator + time.substring(4, 6) + File.separator + time.substring(6, 8);
                    // 修改上传的hdfs的路径格式为/user/log/hive/.../{year}/{month}/{day}/yyyymmddhh
                    currHdfsPath = hdfsPath + timeInfix;
                    LOG.info("current upload hdfs path: " + currHdfsPath);
                    if (merge) {
                        new Thread(getUploadCmd(time, currHdfsPath)).start();
                    }
                }

                time = currTime;
                file = getLocalPath(time);
                try {
                    writer = new PrintWriter(new FileWriter(file));
                } catch (IOException e) {
                    LOG.error(file + " not found");
                    return -1;
                }
            } else {
                try {
                    delivery = consumer.nextDelivery(0);
                    if (delivery == null) {
                        continue;
                    }
                    record = Bytes.toString(delivery.getBody());
                } catch (Exception e) {
                    LOG.error(e);
                    consumer = mq.createConsumer(add);
                    continue;
                }

//                String line = parseRecord(record);
//                if (line.length() != 0) {
//                    writer.println(line);
//                }
            }
        }
    }
}
