package cn.jpush.stat.offline.v2.stats;

import cn.jpush.thrift.StatsDBQueryClient;
import cn.jpush.tool.Alarm;
import cn.jpush.util.SystemConfig;
import org.apache.thrift.TException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by fengwu on 15/6/3.
 */
public class StatsDayGeneralReporter {

    private static final String PLATFORM_ALL = "a,i,w";

    private static final String OFFLINE_HOST = "offline.thrift.host";

    private static final String OFFLINE_PORT = "offline.thrift.port";

    private static final String ONLINE_HOST = "online.thrift.host";

    private static final String ONLINE_PORT = "online.thrift.port";

    private static final String PUSH_NEW_USER = "pushnewuser";

    private static final String PUSH_ONLINE_USER = "pushonlineuser";

    private static final String PUSH_ACTIVE_USER = "pushactiveuser";

    public static void main(String[] args) {

        String statsStartDate = args[0]; // yyyyMMdd
        String statsEndDate = args[1];

        String indexName = args[2];
        String frequency = args[3];

        StatsDBQueryClient offlineClient = new StatsDBQueryClient(SystemConfig.getProperty(OFFLINE_HOST),
                SystemConfig.getIntProperty(OFFLINE_PORT));

        StatsDBQueryClient onlineClient = new StatsDBQueryClient(SystemConfig.getProperty(ONLINE_HOST),
                SystemConfig.getIntProperty(ONLINE_PORT));

        Map<Integer, Double> offlineRegGeneralStats = null;
        Map<Integer, Double> onlineRegGeneralStats = null;
        Map<Integer, Double> offlineOnGeneralStats = null;
        Map<Integer, Double> onlineOnGeneralStats = null;
        Map<Integer, Double> offlineActGeneralStats = null;
        Map<Integer, Double> onlineActGeneralStats = null;

        int begindate = Integer.parseInt(statsStartDate);
        int enddate = Integer.parseInt(statsEndDate);
        try {
            offlineRegGeneralStats = offlineClient.queryDayKPI(PUSH_NEW_USER, begindate,
                    enddate, PLATFORM_ALL);
            onlineRegGeneralStats = onlineClient.queryDayKPI(PUSH_NEW_USER, begindate,
                    enddate, PLATFORM_ALL);

            offlineOnGeneralStats = offlineClient.queryDayKPI(PUSH_ONLINE_USER, begindate,
                    enddate, PLATFORM_ALL);
            onlineOnGeneralStats = onlineClient.queryDayKPI(PUSH_ONLINE_USER, begindate,
                    enddate, PLATFORM_ALL);

            offlineActGeneralStats = offlineClient.queryDayKPI(PUSH_ACTIVE_USER, begindate,
                    enddate, PLATFORM_ALL);
            onlineActGeneralStats = onlineClient.queryDayKPI(PUSH_ACTIVE_USER, begindate,
                    enddate, PLATFORM_ALL);


        } catch (TException e) {
            try {
                Thread.sleep(1000 * 3 * 60);

                offlineRegGeneralStats = offlineClient.queryDayKPI(PUSH_NEW_USER, begindate,
                        enddate, PLATFORM_ALL);
                onlineRegGeneralStats = onlineClient.queryDayKPI(PUSH_NEW_USER, begindate,
                        enddate, PLATFORM_ALL);

                offlineOnGeneralStats = offlineClient.queryDayKPI(PUSH_ONLINE_USER, begindate,
                        enddate, PLATFORM_ALL);
                onlineOnGeneralStats = onlineClient.queryDayKPI(PUSH_ONLINE_USER, begindate,
                        enddate, PLATFORM_ALL);
                offlineActGeneralStats = offlineClient.queryDayKPI(PUSH_ACTIVE_USER, begindate,
                        enddate, PLATFORM_ALL);
                onlineActGeneralStats = onlineClient.queryDayKPI(PUSH_ACTIVE_USER, begindate,
                        enddate, PLATFORM_ALL);

            } catch (Exception e1) {
                e1.printStackTrace();
                Alarm.alarm(64, String.format("%s generate general day report error = %s",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        e.getMessage()));
            }
        }

        // send day report by Alarm
        NumberFormat nf = NumberFormat.getInstance();
        String[] dates = {statsStartDate, statsEndDate};
        StringBuilder description = new StringBuilder();
        for (String date : dates) {
            description.append("|" + date + "|" + nf.format(onlineRegGeneralStats.get(Integer.parseInt(date)))
                    + " / " + nf.format(offlineRegGeneralStats.get(Integer.parseInt(date))) + "|"
                    + nf.format(onlineActGeneralStats.get(Integer.parseInt(date))) + " / "
                    + nf.format(offlineActGeneralStats.get(Integer.parseInt(date))) + "|"
                    + nf.format(onlineOnGeneralStats.get(Integer.parseInt(date))) + " / "
                    + nf.format(offlineOnGeneralStats.get(Integer.parseInt(date))) + "|").append("\n");
        }

        Alarm.alarm(64, String.format("%s 今日统计报表 \n %s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), description.toString()));
    }
}
