package cn.jpush.util;

import cn.jpush.hbase.Hbase;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class KafkaHbaseWriter implements Runnable {

    private static final Logger log = Logger.getLogger(KafkaHbaseWriter.class);

    private KafkaStream stream;
    private int threadNumber;
    private String hname;
    private HTableInterface table;
    protected boolean statif;


    public KafkaHbaseWriter(KafkaStream stream, String hname, int threadNumber, boolean statif) {
        this.stream = stream;
        this.hname = hname;
        this.threadNumber = threadNumber;
        this.statif = statif;
    }

    public void run() {
        table = Hbase.getTable(hname);
        table.setAutoFlushTo(true);
        String crashLog = "";
        ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        while (consumerIterator.hasNext()) {
            byte[] bytes = consumerIterator.next().message();
            crashLog = new String(bytes);
            ArrayList<Put> puts = getPuts(crashLog, mapper);
            if(0 != puts.size()) {
                try {
                    table.put(puts);
                    //table.flushCommits();
                } catch (IOException e) {
                    table = Hbase.getTable(hname);
                    try {
                        table.put(puts);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        log.error("Reconnect HBase due to:" + e);
                        // TODO add alarm here
                    }
                }
            }
        }
    }

    public abstract ArrayList<Put> getPuts(String record, ObjectMapper mapper);
}
