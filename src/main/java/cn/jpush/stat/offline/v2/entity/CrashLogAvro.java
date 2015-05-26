package cn.jpush.stat.offline.v2.entity;

import cn.jpush.util.producer.CrashLogGen;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by fengwu on 15/4/24.
 */
public class CrashLogAvro {

    protected static final Logger LOG = Logger.getLogger(CrashLogAvro.class);

    private Long uid;
    private GenericData.Array<GenericData.Record> crashLogs;
    private String app_key;
    private String platform;
    private int itime;
    private Map<String, String> deviceInfo;

    public static Schema crashLogSchema;
    public static Schema crashLogsSchema;

    public static final byte[] CRASH_STATS_FAMILY = Bytes.toBytes("stats");

    static {
        try {
            InputStream in = CrashLogGen.class.getResourceAsStream("/avro/crashlog_v2.avsc");
            crashLogSchema = new Schema.Parser().parse(in);
            //contentRecordSchema = crashLogSchema.getField("content").schema().getElementType();
            crashLogsSchema = crashLogSchema.getField("crashlogs").schema().getElementType();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("initial crash log schema file error: " + e.getMessage());
        }
    }

    public CrashLogAvro() {
    }

    public CrashLogAvro(Long uid, GenericData.Array<GenericData.Record> crashLogs,
                        String app_key,
                        String platform,
                        int itime,
                        Map<String, String> deviceInfo) {
        this.uid = uid;
        this.crashLogs = crashLogs;
        this.app_key = app_key;
        this.platform = platform;
        this.itime = itime;
        this.deviceInfo = deviceInfo;
    }

    public GenericData.Record serialize() {
        GenericData.Record record = new GenericData.Record(crashLogSchema);

        record.put("uid", uid);
        record.put("crashlogs", crashLogs);
        record.put("platform", platform);
        record.put("app_key", app_key);
        record.put("itime", itime);
        record.put("device_info",deviceInfo);

        return record;
    }

    public Map<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public GenericData.Array<GenericData.Record> getCrashLogs() {
        return crashLogs;
    }

    public void setCrashLogs(GenericData.Array<GenericData.Record> crashLogs) {
        this.crashLogs = crashLogs;
    }

    public String getAppkey() {
        return app_key;
    }

    public void setAppkey(String app_key) {
        this.app_key = app_key;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
