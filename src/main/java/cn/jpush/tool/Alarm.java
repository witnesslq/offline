package cn.jpush.tool;

import cn.jpush.main.AlarmClient;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

public class Alarm {
    private static final Logger LOG = Logger.getLogger(Alarm.class);
    /*
     * codes for kind of alert
     */
    public static final int FAIL_MR = 41;
    public static final int FAIL_CMD = 42;
    public static final int MISSING_INPUT = 43;

    public static void alarm(int code, String desc) {
        AlarmClient client = new AlarmClient();
        try {
            client.sendAlarm(code, desc);
            LOG.info("send alarm: code=" + code + ",desc=" + desc);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException: " + e.getMessage());
        }
    }
}
