package cn.jpush.stat.offline.v2.entity;

import org.apache.avro.generic.GenericData;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Map;

/**
 * Created by fengwu on 15/4/28.
 */
public class ContentRecordAvro {
    private String type;
    private GenericData.Array<GenericData.Record> crashLogsRecords;
    private Map<String, String> deviceInfo;
    private int itime;

    public static final byte[] DEVICE_INFO_FAMILY = Bytes.toBytes("device_info");

    public ContentRecordAvro(String type, GenericData.Array<GenericData.Record> crashLogsRecords,
                             Map<String, String> deviceInfo, int itime) {
        this.type = type;
        this.crashLogsRecords = crashLogsRecords;
        this.deviceInfo = deviceInfo;
        this.itime = itime;
    }

    public GenericData.Record serialize() {
        // initial content record
//        GenericData.Record record = new GenericData.Record(CrashLogAvro.contentRecordSchema);
//
//        record.put("type", type);
//        record.put("crashlogs", crashLogsRecords);
//        record.put("device_info", deviceInfo);
//        record.put("itime", itime);
//
//        return record;
        return null;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GenericData.Array<GenericData.Record> getCrashLogsRecords() {
        return crashLogsRecords;
    }

    public void setCrashLogsRecords(GenericData.Array<GenericData.Record> crashLogsRecords) {
        this.crashLogsRecords = crashLogsRecords;
    }

    public Map<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public int getItime() {
        return itime;
    }

    public void setItime(int itime) {
        this.itime = itime;
    }
}

