package cn.jpush.stat.offline.v2.entity;

import org.apache.avro.generic.GenericData;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Map;

/**
 * Created by fengwu on 15/4/28.
 */
public class CrashLogsAvroRecord {
    private long uid;
    private String appKey;
    private String platform;
    private String message;
    private String versioncode;
    private String networktype;
    private int count;
    private String stacktrace;
    private String versionname;
    private long crashtime;
    private Map<String, String> deviceInfo;

    public static final byte[] CRASH_DETAIL_FAMILY = Bytes.toBytes("detail");

    public CrashLogsAvroRecord(String message,
                               String versioncode,
                               String networktype,
                               int count,
                               String stacktrace,
                               String versionname,
                               long crashtime,
                               Map<String, String> deviceInfo,
                               String appKey,
                               long uid,
                               String platform) {
        this.message = message;
        this.versioncode = versioncode;
        this.networktype = networktype;
        this.count = count;
        this.stacktrace = stacktrace;
        this.versionname = versionname;
        this.crashtime = crashtime;
        this.deviceInfo = deviceInfo;
        this.appKey = appKey;
        this.platform = platform;
        this.uid = uid;
    }

    public CrashLogsAvroRecord(String message,
                               String versioncode,
                               String networktype,
                               int count,
                               String stacktrace,
                               String versionname,
                               long crashtime) {

        this.message = message;
        this.versioncode = versioncode;
        this.networktype = networktype;
        this.count = count;
        this.stacktrace = stacktrace;
        this.versionname = versionname;
        this.crashtime = crashtime;
    }

    public GenericData.Record serialize() {
        GenericData.Record record = new GenericData.Record(CrashLogAvro.crashLogsSchema);
        record.put("message", message);
        record.put("versioncode", versioncode);
        record.put("networktype", networktype);
        record.put("count", count);
        record.put("stacktrace", stacktrace);
        record.put("versionname", versionname);
        record.put("crashtime", crashtime);
        return record;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getNetworktype() {
        return networktype;
    }

    public void setNetworktype(String networktype) {
        this.networktype = networktype;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public long getCrashtime() {
        return crashtime;
    }

    public void setCrashtime(long crashtime) {
        this.crashtime = crashtime;
    }

    public Map<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
