package cn.jpush.stat.offline.v2.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

/**
 * Created by fengwu on 15/4/2.
 */
public class CallTimesMaxStats extends AbstractHiveStats {

    private static Logger logger = LoggerFactory.getLogger(CallTimesMaxStats.class);

    private static final String KPI_CODE= "pushcalltimesmax";

    public CallTimesMaxStats(String kpiCode) {
        super(kpiCode);
    }

    @Override
    public String parseHiveStatsHql() {
        return null;
    }

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String envFlag = args[0];
        String statsDate = args[1];
        String indexName = args[2];
        String frequency = args[3];
        String tableFlag = args[4];

        CallTimesMaxStats hiveStats  = new CallTimesMaxStats(KPI_CODE);
        ResultSet ret = hiveStats.stats(envFlag, indexName, statsDate, frequency, tableFlag);
        hiveStats.save(ret, false, true);

        logger.info(" CallTimesMaxStats.main: all  time =" + (System.currentTimeMillis() - startTime));
    }


}
