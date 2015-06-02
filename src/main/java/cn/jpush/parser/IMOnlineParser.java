package cn.jpush.parser;

import cn.jpush.util.KafkaLocalWritter;
import kafka.consumer.KafkaStream;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by fengwu on 15/6/1.
 *
 * <record>
 {
    "total": 1,"rows": [
        { "uid": 100001, "juid": 23848312, "username": "test", "platform": "a", "appkey": "fdc90b8283db9a439cbf1f14", "itime": 1429061278426 }
    ]
 }
 *
 * </record>
 * platform: a/i/w
 * itime: ms
 *
 *
 *
 */
public class IMOnlineParser extends KafkaLocalWritter {

    public IMOnlineParser(KafkaStream stream, int threadNumber, boolean merge, String pathL, String pathH) {
        super(stream, threadNumber, merge, pathL, pathH);
    }

    @Override
    public List<String> parseRecord(String record, ObjectMapper mapper) throws IOException {
        return null;
    }
}
