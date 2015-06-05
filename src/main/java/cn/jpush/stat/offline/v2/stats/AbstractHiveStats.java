package cn.jpush.stat.offline.v2.stats;

import cn.jpush.util.CalendarUtil;
import cn.jpush.util.DBHelper;

import static cn.jpush.util.StatConstantUtil.EXTERNAL_TABLE;
import static cn.jpush.util.StatConstantUtil.INNER_TABLE;

import cn.jpush.util.SystemConfig;
import cn.jpush.utils.statsdb.DbPool;
import cn.jpush.utils.statsdb.SaveStatsData;
import cn.jpush.utils.statsdb.StatVo;
import com.google.common.base.Strings;
import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengwu on 15/3/30.
 */
public abstract class AbstractHiveStats {

    protected Connection connHive;

    protected ResultSet hiveRet;

    protected HiveStatement hiveStatement;

    protected String frequency;

    protected String statsDate;

    protected String indexName;

    protected String kpiCode;

    protected String envFlag;

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public String getEnvFlag() {
        return envFlag;
    }

    public void setEnvFlag(String envFlag) {
        this.envFlag = envFlag;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Connection getConnHive() {
        return connHive;
    }

    public void setConnHive(Connection connHive) {
        this.connHive = connHive;
    }

    public ResultSet getHiveRet() {
        return hiveRet;
    }

    public void setHiveRet(ResultSet hiveRet) {
        this.hiveRet = hiveRet;
    }

    public HiveStatement getHiveStatement() {
        return hiveStatement;
    }

    public void setHiveStatement(HiveStatement hiveStatement) {
        this.hiveStatement = hiveStatement;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(String statsDate) {
        this.statsDate = statsDate;
    }

    private static Logger logger = LoggerFactory.getLogger(AbstractHiveStats.class);

    public AbstractHiveStats(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public abstract String parseHiveStatsHql();

    private static final String db_key = "stat-mysql";

    public ResultSet stats(String envFlag, String indexName, String statsDate,
                           String frequency, String tableFlag) throws Exception {
        logger.info("AbstractHiveStats.stats:indexName=" + indexName + ";statsDate=" + statsDate
                + ";tableType=" + frequency);

        long startTime = System.currentTimeMillis();
        this.statsDate = statsDate;
        this.indexName = indexName;
        this.frequency = frequency;
        this.envFlag = envFlag;
        String statsYear = statsDate.substring(0, 4);
        String statsMonth = statsDate.substring(4, 6);
        String statsDay = statsDate.substring(6, 8);
        String statsSql = "";
        if (INNER_TABLE.equals(tableFlag)) {
            if ("hour".equals(frequency)) {
                statsSql =
                        SystemConfig.getProperty(indexName + ".v2.stats.sql.prefix")
                                + CalendarUtil.getCondition(statsDate, "hour")
                                + SystemConfig.getProperty(indexName + ".v2.stats.sql.postfix");

            } else if ("day".equals(frequency)) {
                statsSql =
                        SystemConfig.getProperty(indexName + ".v2.stats.sql.prefix")
                                + CalendarUtil.getCondition(statsDate, "day")
                                + SystemConfig.getProperty(indexName + ".v2.stats.sql.postfix");

            } else if ("month".equals(frequency)) {
                statsSql =
                        SystemConfig.getProperty(indexName + ".v2.stats.sql.prefix")
                                + CalendarUtil.getCondition(statsDate, "month")
                                + SystemConfig.getProperty(indexName + ".v2.stats.sql.postfix");

            }
        }
        if (EXTERNAL_TABLE.equals(tableFlag)) {
            Object[] statsDateArray = {};
            if ("hour".equals(frequency)) {
                statsDateArray = new Object[]{statsYear, statsMonth, statsDay, statsDate};
                statsSql = SystemConfig.getProperty(indexName + ".v2.stats.hour.sql");
            }
            if ("day".equals(frequency)) {
                statsDateArray = new Object[]{statsYear, statsMonth, statsDay, statsDate.substring(0, 8)};
                statsSql = SystemConfig.getProperty(indexName + ".v2.stats.day.sql");

            }
            statsSql = MessageFormat.format(statsSql, statsDateArray);
        }

        long nowTime = System.currentTimeMillis();

        connHive = DBHelper.getHiveConn(envFlag);
        hiveStatement = (HiveStatement) connHive.createStatement();

        logger.info("AbstractHiveStats.stats:statsSql=" + statsSql);

        if (!Strings.isNullOrEmpty(statsSql)) {
            hiveRet = hiveStatement.executeQuery(statsSql);
        }

        logger.info("AbstractHiveStats.stats:query time ="
                + (System.currentTimeMillis() - nowTime));

        return hiveRet;
    }

    public ResultSet stats(String sql) throws Exception {
        long nowTime = System.currentTimeMillis();
        connHive = DBHelper.getHiveConn(envFlag);
        hiveStatement = (HiveStatement) connHive.createStatement();

        hiveRet = hiveStatement.executeQuery(sql);

        logger.info("AbstractHiveStats.stats:query time ="
                + (System.currentTimeMillis() - nowTime));

        return hiveRet;
    }


    public void save(ResultSet resultSet, boolean havaPlatform, boolean generalFlag) throws SQLException {
        long startTime = System.currentTimeMillis();

        StatVo statVo = null;
        List<StatVo> list = new ArrayList<StatVo>();
        String platform = "A";

        //initialize c3p0 properties
        Connection conn = DbPool.getConnection(db_key);
        int total = 0;
        while (resultSet.next()) {
            total++;
            String appKey = resultSet.getString("appkey");

            if (havaPlatform) {
                platform = resultSet.getString("platform");
            }
            long cnt = 0L;
            cnt = resultSet.getLong("cnt");

            statVo = new StatVo();
            statVo.setAppkey(appKey);
            statVo.setPlatform(platform);
            statVo.setValue(cnt);
            list.add(statVo);
        }

        try {
            if ("hour".equals(frequency)) {
                SaveStatsData.saveHourKPI("off", kpiCode, Integer.valueOf(statsDate.substring(0, 8)), Integer.valueOf(statsDate.substring(8)), list, generalFlag);
            } else if ("day".equals(frequency)) {
                SaveStatsData.saveKPI("off", "d", kpiCode, Integer.valueOf(statsDate.substring(0, 8)), list, generalFlag);
            } else if ("month".equals(frequency)) {
                SaveStatsData.saveKPI("off", "m", kpiCode, Integer.valueOf(statsDate.substring(0, 6)), list, generalFlag);
            }
            logger.info(String.format("AbstractHiveStats.save:commit %d cost %d "), list.size(), System.currentTimeMillis() - startTime);
            list.clear();

        } catch (Exception e) {
            new Throwable(new SQLException(e));
        }

        logger.info("AbstractHiveStats.save:all time =" + (System.currentTimeMillis() - startTime) + " and save counts = " + total);
        clearHiveConnResources();
        list.clear();
    }

    public void clearHiveConnResources() throws SQLException {
        hiveRet.close();
        hiveStatement.close();
        DBHelper.closeHiveConn(connHive);
    }
}



