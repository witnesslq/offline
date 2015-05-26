package cn.jpush.stat.offline.v2.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

/**
 * Created by fengwu on 15/3/30.
 */
public class CallTimesStats extends AbstractHiveStats{

    private static Logger logger = LoggerFactory.getLogger(CallTimesStats.class);

    private static final String KPI_CODE= "pushcalltimes";

    public CallTimesStats(String kpiCode) {
        super(kpiCode);
    }

    @Override
    public String parseHiveStatsHql() {
        return null;
    }

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];
        String frequency = args[4];
        String tableFlag = args[5];

        CallTimesStats hiveStats  = new CallTimesStats(KPI_CODE);
        ResultSet ret = hiveStats.stats(envFlag, indexName, statsDate, frequency, tableFlag);
        hiveStats.save(ret, false, true);

        logger.info(" CallTimesStats.main: all  time =" + (System.currentTimeMillis() - startTime));
    }
}
