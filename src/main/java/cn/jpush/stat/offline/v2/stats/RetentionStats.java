package cn.jpush.stat.offline.v2.stats;

import cn.jpush.stat.offline.v2.entity.Platform;
import cn.jpush.util.CalendarUtil;
import cn.jpush.util.MySqLImporter;
import cn.jpush.util.SystemConfig;
import cn.jpush.utils.statsdb.StatVo;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengwu on 15/4/17.
 */
public class RetentionStats extends AbstractHiveStats {

    private static Logger logger = LoggerFactory.getLogger(ClickTimes.class);

    private static final String KPI_CODE = "retention";

    public static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    private static Map<String, List<StatVo>> statVoMaps = new HashMap<String, List<StatVo>>();

    private int internal;

    public int getInternal() {
        return internal;
    }

    public void setInternal(int internal) {
        this.internal = internal;
    }

    private String regDate;

    public RetentionStats(String kpiCode) {
        super(kpiCode);
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public static void save(RetentionStats hiveStats, boolean havePlatform, boolean generalFlag)
            throws SQLException {
        double kpiValue = 0;
        ResultSet resultSet = hiveStats.getHiveRet();
        List<StatVo> statVoA = new ArrayList<StatVo>();
        List<StatVo> statVoI = new ArrayList<StatVo>();
        List<StatVo> statVoW = new ArrayList<StatVo>();

        Platform platform = null;
        int count = 0;
        while (resultSet.next()) {
            count++;
            StatVo statVo = new StatVo();
            String appKey = resultSet.getString("appkey");
            statVo.setAppkey(appKey);
            if (havePlatform) {
                String platformValue = resultSet.getString("platform").toLowerCase();
                statVo.setPlatform(platformValue);
                platform = Platform.valueOf(platformValue);
            }
            kpiValue = resultSet.getDouble("kpi");
            statVo.setValue(kpiValue);
            switch (platform) {
                case a:
                    statVoA.add(statVo);
                    break;
                case i:
                    statVoI.add(statVo);
                    break;
                case w:
                    statVoW.add(statVo);
                    break;
                default:
                    logger.error("error data:" + appKey + " " + platform + " " + platform);
            }
        }
        logger.info("RetentionStats:" + "get" + count + "records ");
        statVoMaps.put("a", statVoA);
        statVoMaps.put("i", statVoI);
        statVoMaps.put("w", statVoW);

        hiveStats.clearHiveConnResources();
    }

    class DBImporter implements Runnable {
        private int internal;

        private RetentionStats hiveStats;

        public DBImporter(RetentionStats hiveStats, int internal) {
            this.internal = internal;
            this.hiveStats = hiveStats;
        }

        public void run() {
            try {
                MySqLImporter.saveMysql(hiveStats, statVoMaps, internal);
            } catch (SQLException e) {
                new Throwable(new SQLException(e));
            }
        }
    }

    @Override
    public String parseHiveStatsHql() {
        long startTime = System.currentTimeMillis();
        String statsYear = statsDate.substring(0, 4);
        String statsMonth = statsDate.substring(4, 6);
        String statsDay = statsDate.substring(6, 8);
        String statsSql = "";

        Object[] statsParameterArray = {};
        if ("hour".equals(frequency)) {
            /*statsDateArray = new Object[]{statsYear, statsMonth, statsDay, statsDate};
            statsSql = SystemConfig.getProperty(indexName + ".v2.stats.hour.sql");*/
        }
        if ("day".equals(frequency)) {
            statsParameterArray = new Object[]{statsDate, internal};
            statsSql = SystemConfig.getProperty(indexName + ".v2.stats.day.sql");

        }
        statsSql = MessageFormat.format(statsSql, statsParameterArray);
        logger.info("RetentionStats.stats:statsSql=" + statsSql);
        return statsSql;
    }

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String envFlag = args[0];
        String statsDate = args[1];
        String indexName = args[2];
        String frequency = args[3];

        int[] rateArray = new int[]{1, 2, 3, 4, 5, 6, 7, 14, 30};

        long currentStatsTimes = CalendarUtil.transTimeStrToStamp(statsDate, df);
        for (int rate : rateArray) {
            String regDate = CalendarUtil.format("yyyyMMdd", currentStatsTimes - rate * 60 * 60 * 24 * 1000L);
            RetentionStats hiveStats = new RetentionStats(KPI_CODE);
            hiveStats.setEnvFlag(envFlag);
            hiveStats.setIndexName(indexName);
            hiveStats.setStatsDate(statsDate);
            hiveStats.setRegDate(regDate);
            hiveStats.setFrequency(frequency);
            hiveStats.setInternal(rate);
            String statsHql = hiveStats.parseHiveStatsHql();
            hiveStats.stats(statsHql);
            save(hiveStats, true, false);

            Runnable importRunner = hiveStats.new DBImporter(hiveStats, rate);
            Thread importThread = new Thread(importRunner);
            importThread.setName("kpi_" + rate);
            importThread.start();
        }
        logger.info(" RetentionStats.main: all  time =" + (System.currentTimeMillis() - startTime));
    }
}
