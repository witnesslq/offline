package cn.jpush.stat.offline.v2.stats;

import cn.jpush.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.text.MessageFormat;

/**
 * Created by fengwu on 15/3/30.
 */
public class ClickTimes extends AbstractHiveStats {

    private static Logger logger = LoggerFactory.getLogger(ClickTimes.class);

    private static final String KPI_CODE= "pushclicktimes";


    public ClickTimes(String kpiCode) {
        super(kpiCode);
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
            statsParameterArray = new Object[]{statsYear, statsMonth, statsDay, statsDate.substring(8)};
            statsSql = SystemConfig.getProperty(indexName + ".v2.stats.hour.sql");
        }
        if ("day".equals(frequency)) {
            statsParameterArray = new Object[]{statsYear, statsMonth, statsDay};
            statsSql = SystemConfig.getProperty(indexName + ".v2.stats.day.sql");

        }
        statsSql = MessageFormat.format(statsSql, statsParameterArray);
        logger.info("ClickTimes.stats:statsSql=" + statsSql);
        return statsSql;
    }

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];
        String frequency = args[4];
        String tableFlag = args[5];

        ClickTimes hiveStats  = new ClickTimes(KPI_CODE);
        hiveStats.setEnvFlag(envFlag);
        hiveStats.setIndexName(indexName);
        hiveStats.setStatsDate(statsDate);
        hiveStats.setFrequency(frequency);
        //ResultSet ret = hiveStats.stats(envFlag, indexName, statsDate, frequency, tableFlag);
        String statsHql = hiveStats.parseHiveStatsHql();
        hiveStats.stats(statsHql);
        hiveStats.save(hiveStats.getHiveRet(), true, false);

        logger.info(" ClickTimes.main: all  time =" + (System.currentTimeMillis() - startTime));
    }
}
