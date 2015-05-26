package cn.jpush.util;

import cn.jpush.stat.offline.v2.entity.CrashLogAvro;
import cn.jpush.tool.Alarm;
import cn.jpush.tool.CommandWithAlarm;
import cn.jpush.tool.Time;
import cn.jpush.tool.UploadToHdfs;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by fengwu on 15/4/22.
 */

public abstract class KafkaLocalWritter  implements Runnable {
    protected static final Logger LOG = Logger.getLogger(KafkaLocalWritter.class);
    public static final String TEMP = ".tmp.";
    public static final String UNKNOWN = "unknown";
    private int ttl = 3;
    private KafkaStream stream;
    private int threadNumber;
    private boolean merge;
    private String localPath;
    private String hdfsPath;

    public KafkaLocalWritter(KafkaStream stream, int threadNumber, boolean merge, String pathL, String pathH) {
        this.stream = stream;
        this.threadNumber = threadNumber;
        this.merge = merge;
        localPath = pathL;
        hdfsPath = pathH;
        if (new File(localPath).mkdirs()) {
            LOG.info("create the Dir " + localPath);
        }
    }

    public String getLocalPath(String file) {
        return localPath + File.separator + threadNumber + TEMP + file;
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

    public void run() {
        String time = Time.format("yyyymmddhh", System.currentTimeMillis());
        String file = getLocalPath(time);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            LOG.error(file + " not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        //initial avro reader
//        DatumReader<GenericData.Record> reader = new GenericDatumReader<GenericData.Record>(CrashLogAvro.crashLogSchema);

        String currTime;
        String record;
        String currHdfsPath;

        String initTimeInfix = time.substring(0, 4) + File.separator + time.substring(4, 6) + File.separator + time.substring(6, 8);
        // 修改上传的hdfs的路径格式为/user/log/hive/.../{year}/{month}/{day}/yyyymmddhh
        String initHdfsPath = hdfsPath + initTimeInfix;
        LOG.info(Thread.currentThread().getName() + " initial upload hdfs path: " + initHdfsPath);
        currHdfsPath = initHdfsPath;
        ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();
        while (consumerIterator.hasNext()) {
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
                }
            } else {
                try {
                    byte[] bytes = consumerIterator.next().message();
//                    InputStream in = new ByteArrayInputStream(bytes);
//                    JsonDecoder decoder = DecoderFactory.get().jsonDecoder(CrashLogAvro.crashLogSchema, in);
//                    reader.read(null, decoder);
                    record = new String(bytes);
                    LOG.debug("read record: " + record);
                    List<String> lines = parseRecord(record, mapper);
                    if (!lines.isEmpty()) {
                        for(String line : lines){
                            writer.println(line);
                        }
                    }
                } catch (JsonMappingException e) {
                    LOG.error("json format error " + e.getMessage());
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    LOG.error("json parse error " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SerializationException e) {
                    LOG.error("serialize error" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public abstract List<String> parseRecord(String record, ObjectMapper mapper) throws IOException;

}

