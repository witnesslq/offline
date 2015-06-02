package cn.jpush.parser;

import cn.jpush.stat.offline.v2.entity.Platform;
import cn.jpush.stat.offline.v2.entity.Recv;
import cn.jpush.util.KafkaLocalWritter;
import cn.jpush.util.StatConstantUtil;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengwu on 15/6/1.
 *
 * {
        "total": 1,
            "rows": [
                {
                    "uid": 100001,
                    "msgid": 200001,
                    "juid": 23848312,
                    "platform": "a",
                    "appkey": "fdc90b8283db9a439cbf1f14",
                    "itime": 1429061278426
                }
            ]
   }
 *
 */
public class IMRecvParser extends KafkaLocalWritter {

    private long uid;

    private long msgId;

    private long juid;

    private String platform;

    private String appkey;

    private long itime;

    private static final Logger LOG = Logger.getLogger(IMRecvParser.class);

    public IMRecvParser(KafkaStream stream, int threadNumber, boolean merge, String pathL, String pathH) {
        super(stream, threadNumber, merge, pathL, pathH);
    }

    @Override
    public List<String> parseRecord(String record, ObjectMapper mapper) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            Map<String, Object> map = mapper.readValue(record, Map.class);
            int num = Integer.parseInt(String.valueOf(map.get(Recv.NUMBER_RECORDS_RECV)));
            Object rows = map.get(Recv.RECORDS_RECV);
            if (rows instanceof ArrayList) {
                ArrayList<LinkedHashMap> list = (ArrayList<LinkedHashMap>) rows;
                if (num != list.size()) {
                    LOG.info("error records number");
                }

                for (LinkedHashMap hash : list) {
                    msgId = Long.parseLong(String.valueOf(hash.get(Recv.MSGID_RECV)));
                    uid = Long.parseLong(String.valueOf(hash.get(Recv.UID)));
                    itime = Long.parseLong(String.valueOf(hash.get(Recv.ITIME)));
                    platform = String.valueOf(hash.get(StatConstantUtil.PLATFORM)).toLowerCase();
                    juid = Long.parseLong(String.valueOf(hash.get(Recv.JUID)));
                    appkey = String.valueOf(hash.get(Recv.APPKEY_PNS));
                    lines.add(this.toString());
                }
            } else {
                LOG.error("record format error cause by the json field rows is not a array structure");
            }
        } catch (IOException e1) {
            throw new IOException(e1);
        }
        return lines;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(msgId + "\t").append(uid + "\t").append(itime + "\t")
            .append(platform + "\t").append(appkey + "\t").append(juid);
        return sb.toString();
    }
}
