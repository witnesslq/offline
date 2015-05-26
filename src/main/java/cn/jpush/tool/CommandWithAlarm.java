package cn.jpush.tool;

import cn.jpush.tool.Alarm;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandWithAlarm {
    public static final Logger LOG = Logger.getLogger(CommandWithAlarm.class);
    public static final CommandWithAlarm DEFAULT = new CommandWithAlarm(-1, "");

    private int code;
    private String cmd;

    public CommandWithAlarm(int alarmCode, String command) {
        code = alarmCode;
        cmd = command;
    }

    public String getCmd() {
        return cmd;
    }

    public boolean execute() {
        boolean success = false;
        try {
            LOG.info("execute: " + cmd);
            Process p = Runtime.getRuntime().exec(new String[] {"sh", "-c", cmd});
            if (p.waitFor() != 0) {
                LOG.error("fail to execute command: " + cmd);
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = null;
                StringBuilder lines = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    lines.append(line + System.lineSeparator());
                }
                LOG.error(lines);
                Alarm.alarm(code, "fail to execute: " + cmd);
            } else {
                success = true;
            }
        } catch (InterruptedException e) {
            LOG.error("shouldn't appear: " + cmd + " is interrupted");
        } catch (IOException e) {
            LOG.error("shouldn't appear: an I/O error when executing " + cmd);
        }
        return success;
    }
}
