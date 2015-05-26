package cn.jpush.parser;

import cn.jpush.stat.offline.v2.entity.Recv;
import cn.jpush.util.KafkaLocalWritter;
import kafka.consumer.KafkaStream;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengwu on 15/5/14.
 * <p/>
 * <p/>
 * </>
 */
public class MsgRecvParser extends KafkaLocalWritter {

    private static final Logger LOG = Logger.getLogger(MsgRecvParser.class);

    public MsgRecvParser(KafkaStream stream, int threadNumber, boolean merge, String pathL, String pathH) {
        super(stream, threadNumber, merge, pathL, pathH);
    }

    private long msgId;
    private long uid;
    private long itime;

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getItime() {
        return itime;
    }

    public void setItime(long itime) {
        this.itime = itime;
    }

    @Override
    public List<String> parseRecord(String record, ObjectMapper mapper) throws
            IOException {
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
        sb.append(msgId + "\t").append(uid + "\t").append(itime);
        return sb.toString();
    }
}
