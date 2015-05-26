package cn.jpush.tool;

import cn.jpush.hbase.Hbase;
import cn.jpush.tool.Alarm;
import cn.jpush.tool.CommandWithAlarm;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadToHdfs implements Runnable {
    private static final Logger LOG = Logger.getLogger(cn.jpush.tool.UploadToHdfs.class);
    private static final ArrayList<String> SINGLE = new ArrayList<String>();
    private static final HashMap<String, cn.jpush.tool.UploadToHdfs> SINGLE_INSTANCE =
            new HashMap<String, cn.jpush.tool.UploadToHdfs>();

    private String src;
    private String dst;
    private cn.jpush.tool.CommandWithAlarm preCmd;

    private UploadToHdfs(String source, String destination) {
        this(cn.jpush.tool.CommandWithAlarm.DEFAULT, source, destination);
    }

    private UploadToHdfs(cn.jpush.tool.CommandWithAlarm pre, String source, String destination) {
        preCmd = pre;
        src = source;
        dst = destination;
    }

    public static cn.jpush.tool.UploadToHdfs newInstance(String source, String destination) {
        return newInstance(cn.jpush.tool.CommandWithAlarm.DEFAULT, source, destination);
    }

    public static cn.jpush.tool.UploadToHdfs newInstance(cn.jpush.tool.CommandWithAlarm pre, String source, String destination) {
        String singleKey = pre.getCmd() + ":" + source + ":" + destination;
        SINGLE.add(singleKey);

        if (!SINGLE_INSTANCE.containsKey(singleKey)) {
            SINGLE_INSTANCE.put(singleKey, new cn.jpush.tool.UploadToHdfs(pre, source, destination));
        }
        return SINGLE_INSTANCE.get(singleKey);
    }

    public synchronized void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOG.error("sleep is interrupted");
        }
        if (preCmd != CommandWithAlarm.DEFAULT) {
            preCmd.execute();
        }

        String traceInfo = "upload from " + src + " to " + dst;
        try {
            FileSystem fs = FileSystem.get(Hbase.createConf());
            Path hdfs = new Path(dst);
            fs.mkdirs(hdfs);
            fs.copyFromLocalFile(new Path(src), new Path(dst));
            LOG.info(traceInfo);
        } catch (IOException e) {
            Alarm.alarm(Alarm.FAIL_CMD, "fail to " + traceInfo);
        }

        String singleKey = preCmd.getCmd() + ":" + src + ":" + dst;
        SINGLE.remove(singleKey);
        if (!SINGLE.contains(singleKey)) {
            SINGLE_INSTANCE.remove(singleKey);
        }
    }

}
