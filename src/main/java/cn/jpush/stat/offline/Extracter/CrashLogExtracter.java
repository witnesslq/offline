package cn.jpush.stat.offline.Extracter;

import cn.jpush.hbase.Hbase;
import cn.jpush.hbase.Htables;
import cn.jpush.stat.offline.v2.entity.CrashLogAvro;
import cn.jpush.stat.offline.v2.entity.CrashLogsAvroRecord;
import cn.jpush.stat.offline.v2.entity.Platform;
import cn.jpush.stat.offline.v2.stats.Statable;
import cn.jpush.util.KafkaHbaseWriter;
import cn.jpush.util.StatConstantUtil;
import kafka.consumer.KafkaStream;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fengwu on 15/4/22.
 * <p/>
 * use to parse crash log record from kafka,then put to {@link Htables#CRASH_LOG HTable}.
 * and stat crash log by field message(exception types) ,then put to {@link Htables#CRASH_LOG_STATS HTable}
 * A crash log record looks like as follow:
 * <br>RowKey consists of : app_key, platform, Long.MAX_VALUE - crashtime for {@link Htables#CRASH_LOG HTable}</br>
 * <br>column family = detail</>
 * <br>>column qualifier is defined as:</>
 * detail:message
 * detail:versioncode
 * detail:networktype
 * detail:count
 * detail:stacktrace
 * detail:versionname
 * detail:crashtime
 * <p/>
 * <br>RowKey consists of : app_key, platform for {@link Htables#CRASH_LOG_STATS HTable}</br>
 * <br>column family = stats</>
 * <br>>column qualifier is defined as:</>
 * stats:message
 * stats:versionname
 * stats:crashtime
 * stats:counter
 */


public class CrashLogExtracter extends KafkaHbaseWriter implements Statable {

//    public static final String CRUSH_LOG_LOCAL_PATH = "./log_hive/crushLog";
//    public static final String CRUSH_LOG_HDFS_PATH = "/user/log/hive/crushLog/";
//    private static final String FIELD_DELIMITER = "|";

    private static final Logger LOG = Logger.getLogger(CrashLogExtracter.class);

    private CrashLogsAvroRecord crashLogsAvroRecord;

    public CrashLogExtracter(KafkaStream stream, int threadNumber, boolean statif) {
        super(stream, Htables.CRASH_LOG, threadNumber, statif);
    }

//    public CrashLogAvro parse(String record) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//        CrashLogAvro crashLogsAvroRecord = null;
//        try {
//            Map<String, Object> map = mapper.readValue(record, Map.class);
//            long uid = Long.parseLong(String.valueOf(map.get(StatConstantUtil.UID)));
//            String platform = String.valueOf(map.get(StatConstantUtil.PLATFORM));
//            String appkey = String.valueOf(map.get(StatConstantUtil.APPKEY));
//            ArrayList<LinkedHashMap<String, Object>> contentList = (ArrayList<LinkedHashMap<
//                    String, Object>>) map.get("content");
//            LinkedHashMap<String, Object> contentMap = contentList.get(0);
//            int itime = Integer.parseInt(String.valueOf(contentMap.get("itime")));
//            String type = String.valueOf(contentMap.get("type"));
//            ArrayList<LinkedHashMap<String, Object>> crashLogList = (ArrayList<LinkedHashMap<
//                    String, Object>>) contentMap.get("crashlogs");
//            // crash detail
//            LinkedHashMap<String, Object> crashLogMap = crashLogList.get(0);
//            String message = String.valueOf(crashLogMap.get("message"));
//            String versioncode = String.valueOf(crashLogMap.get("versioncode"));
//            String networktype = String.valueOf(crashLogMap.get("networktype"));
//            int count = Integer.parseInt(String.valueOf(crashLogMap.get("count")));
//            String stacktrace = String.valueOf(crashLogMap.get("stacktrace"));
//            String versionname = String.valueOf(crashLogMap.get("versionname"));
//            long crashtime = Long.parseLong(String.valueOf(crashLogMap.get("crashtime")));
//            LinkedHashMap<String, String> deviceInfo = (LinkedHashMap<String, String>) map.get("device_info");
//            // initial crash logs record
//            CrashLogsAvroRecord crashLogsAvroRecord = new CrashLogsAvroRecord(message, versioncode,
//                    networktype, count, stacktrace, versionname, crashtime);
//
//            GenericData.Array<GenericData.Record> crashLogsRecords = new GenericData.Array<GenericData.Record>(1,
//                    CrashLogAvro.contentRecordSchema.getField("crashlogs").schema());
//            crashLogsRecords.add(crashLogsAvroRecord.serialize());
//
//            // initial content record
//            ContentRecordAvro contentRecordAvro = new ContentRecordAvro(type, crashLogsRecords, deviceInfo, itime);
//            // initial crash log record
//            GenericData.Array<GenericData.Record> content = new GenericData.Array<GenericData.Record>(1,
//                    CrashLogAvro.crashLogSchema.getField("content").schema());
//            content.add(contentRecordAvro.serialize());
//
//            crashLogsAvroRecord = new CrashLogAvro(uid, content, appkey, platform);
//
//        } catch (Exception e) {
//            LOG.error(record);
//            LOG.error("can't parse cause " + e.getMessage());
//        }
//
//        return crashLogsAvroRecord;
//    }

    private static CrashLogsAvroRecord parseRecord(String record, ObjectMapper mapper) throws IOException {
        LOG.info("read crash log from kafka:" + record);
        CrashLogsAvroRecord crashLog = null;
        String message = "";
        String versioncode = "";
        String networktype = "";
        int count = 0;
        String stacktrace = "";
        String versionname = "";
        long crashtime = 0L;
        LinkedHashMap<String, String> deviceInfo = null;
        long uid = 0L;
        String platformStr = "";
        String appKey = "";
        Map<String, Object> map = mapper.readValue(record, Map.class);
        uid = Long.parseLong(String.valueOf(map.get(StatConstantUtil.UID)));
        platformStr = String.valueOf(map.get(StatConstantUtil.PLATFORM)).toLowerCase();
        Platform platform = Platform.valueOf(platformStr);
        appKey = String.valueOf(map.get(StatConstantUtil.APPKEY));

        ArrayList<LinkedHashMap<String, Object>> crashLogList = (ArrayList<LinkedHashMap<String,
                Object>>) map.get("crashlogs");
        if (null == crashLogList) {
            crashLogList = (ArrayList<LinkedHashMap<String,
                    Object>>) map.get("crashLogs");
            if (null == crashLogList) {
                return null;
            }
        }
        if (null != crashLogList && 0 != crashLogList.size()) {
            LinkedHashMap<String, Object> crashLogMap = crashLogList.get(0);
            message = String.valueOf(crashLogMap.get("message"));
            versioncode = String.valueOf(crashLogMap.get("versioncode"));
            networktype = String.valueOf(crashLogMap.get("networktype"));
            count = Integer.parseInt(String.valueOf(crashLogMap.get("count")));
            versionname = String.valueOf(crashLogMap.get("versionname"));
            crashtime = Long.parseLong(String.valueOf(crashLogMap.get("crashtime")));

            // crash log stacktrace 的解析不同平台不同
            switch (platform) {
                case a:
                    deviceInfo = (LinkedHashMap<String, String>) map.get("device_info");
                    stacktrace = String.valueOf(crashLogMap.get("stacktrace"));
                    break;
                case i:
                    deviceInfo = (LinkedHashMap<String, String>) crashLogMap.get("device_info");
                    ArrayList<String> stacktraceList = (ArrayList<String>) crashLogMap.get("stacktrace");
                    StringBuilder stacktraceSB = new StringBuilder();
                    for (String st : stacktraceList) {
                        stacktraceSB.append(st).append("\n\t");
                    }
                    stacktrace = stacktraceSB.deleteCharAt(stacktraceSB.lastIndexOf("\t")).toString();
                    break;
                case w:
                    break;
                default:
                    System.out.println("error data:" + appKey + " " + platform + " " + platform);
            }

            crashLog = new CrashLogsAvroRecord(message,
                    versioncode, networktype, count, stacktrace, versionname, crashtime, deviceInfo, appKey, uid, platformStr);
            LOG.info("parse crash log success ");
        } else {
            LOG.error("read bad data from kafka: " + record);
        }

        return crashLog;
    }

    @Override
    public ArrayList<Put> getPuts(String record, ObjectMapper mapper) {
        ArrayList<Put> puts = new ArrayList<Put>();
        Put put = null;
        try {
            crashLogsAvroRecord = parseRecord(record, mapper);
        } catch (IOException e) {
            LOG.error("read bad data from kafka: " + record);
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        if (null != crashLogsAvroRecord) {
            long uid = crashLogsAvroRecord.getUid();
            String platform = crashLogsAvroRecord.getPlatform();
            String appkey = crashLogsAvroRecord.getAppKey();

            String message = crashLogsAvroRecord.getMessage();
            String versioncode = crashLogsAvroRecord.getVersioncode();
            String networktype = crashLogsAvroRecord.getNetworktype();
            int count = crashLogsAvroRecord.getCount();
            String stacktrace = crashLogsAvroRecord.getStacktrace();
            String versionname = crashLogsAvroRecord.getVersionname();
            long crashtime = crashLogsAvroRecord.getCrashtime();

            long rowfix = Long.MAX_VALUE - crashtime;

            // device_info

            // put hbase
            put = new Put(Bytes.toBytes(appkey + platform + rowfix));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("message"), crashtime, Bytes.toBytes(message));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("versioncode"), crashtime, Bytes.toBytes(versioncode));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("networktype"), crashtime, Bytes.toBytes(networktype));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("count"), crashtime, Bytes.toBytes(count));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("stacktrace"), crashtime, Bytes.toBytes(stacktrace));
            put.add(CrashLogsAvroRecord.CRASH_DETAIL_FAMILY, Bytes.toBytes("versionname"), crashtime, Bytes.toBytes(versionname));

            puts.add(put);

            if (statif) {
                stats();  // 累加器
            }
        }
        return puts;
    }

    public void stats() {
        HTableInterface statTable = Hbase.getTable(Htables.CRASH_LOG_STATS);
        statTable.setAutoFlushTo(true);
        String platform = crashLogsAvroRecord.getPlatform();
        String appkey = crashLogsAvroRecord.getAppKey();

        String message = crashLogsAvroRecord.getMessage();
        String versionname = crashLogsAvroRecord.getVersionname();
        long crashtime = crashLogsAvroRecord.getCrashtime();
        try {
            long cnt = statTable.incrementColumnValue(Bytes.toBytes(appkey + "|" + platform + "|" + message + "|" + versionname),
                    CrashLogAvro.CRASH_STATS_FAMILY, Bytes.toBytes("total"), 1);
            LOG.info("current stats count: " + cnt);
            //statTable.flushCommits();
        } catch (IOException e) {
            e.printStackTrace();
            statTable = Hbase.getTable(Htables.CRASH_LOG_STATS);
            try {
                statTable.incrementColumnValue(Bytes.toBytes(appkey + "|" + platform + "|" + message + "|" + versionname),
                        CrashLogAvro.CRASH_STATS_FAMILY, Bytes.toBytes("total"), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
                LOG.error("Reconnect HBase due to:" + e);
                // TODO add alarm here
            }
        }
    }
}
