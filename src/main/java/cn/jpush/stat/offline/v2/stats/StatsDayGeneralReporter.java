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

    public enum KPICode{
        NEW_USER("pushnewuser"), ONLINE_USER("pushonlineuser") , ACTIVE_USER("pushactiveuser");

        private String kpiCodeStr;

        KPICode(String kpiCodeStr) {
            this.kpiCodeStr = kpiCodeStr;
        }

        @Override
        public String toString() {
            return kpiCodeStr;
        }
    }

    public static void main(String[] args) {

        String statsStartDate = args[0]; // yyyyMMdd
        String statsEndDate = args[1];

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
        boolean normal = false;
        boolean regNormal = false;
        boolean activeNormal = false;
        boolean onlineNormal = false;
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

        NumberFormat nf = NumberFormat.getInstance();
        regNormal = isNormal(KPICode.NEW_USER, offlineRegGeneralStats.get(begindate),
                onlineRegGeneralStats.get(begindate));
        onlineNormal = isNormal(KPICode.ONLINE_USER, offlineOnGeneralStats.get(begindate),
                onlineOnGeneralStats.get(begindate));

        activeNormal = isNormal(KPICode.ACTIVE_USER, offlineActGeneralStats.get(begindate),
                onlineActGeneralStats.get(begindate));
        normal = regNormal && activeNormal && onlineNormal;
        //System.out.println("normal=" + normal);

        // send day report by Alarm
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

        if(normal){
            description.append("统计正常");
        }else {
            description.append("统计异常，请检查!!");
        }

        Alarm.alarm(64, String.format("%s 今日统计报表 \n %s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), description.toString()));
    }

    private static boolean isNormal(KPICode kpiCode, Double off, Double on){
        boolean normal = false;
        switch (kpiCode){
            case NEW_USER:
                normal = Math.abs(off - on) < 1000;
                break;
            case ACTIVE_USER:
                normal = Math.abs(off - on ) < 50 * 10000;
                break;
            case ONLINE_USER:
                normal = Math.abs(off - on) < 10 * 10000;
                break;
            default:
                break;
        }
        return normal;
    }
}
