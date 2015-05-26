package cn.jpush.util;

import java.nio.MappedByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.jpush.stat.offline.v2.stats.AbstractHiveStats;
import cn.jpush.stat.offline.v2.stats.RetentionStats;
import cn.jpush.utils.statsdb.StatVo;
import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqLImporter {

    private static Logger logger = LoggerFactory.getLogger(MySqLImporter.class);

    public static void distinctDomainStats(String envFlag, String statsType, String statsDate, String platform,
                                           HashMap<String, Long> distributions) throws SQLException {
        long startTime = System.currentTimeMillis();
        Connection connSQL = null;
        PreparedStatement sqlStatement = null;
        try {
            logger.info("ThreadName:" + Thread.currentThread().getName() +
                    ";MysqLImporter.distinctDomainStats:statsType=" + statsType);

            String statsSql = SystemConfig.getProperty("mysql.distinct.distribution.day");

            DBHelper.setDataSource(envFlag);
            connSQL = DBHelper.getMySQLConn();
            sqlStatement = connSQL.prepareStatement(statsSql);

            for (Entry<String, Long> distribution : distributions.entrySet()) {
                sqlStatement.setString(1, distribution.getKey());
                sqlStatement.setString(2, platform);
                sqlStatement.setString(3, statsDate);
                sqlStatement.setString(4, statsType);
                sqlStatement.setLong(5, distribution.getValue());
                sqlStatement.execute();
            }
        } catch (SQLException e) {
            logger.error("ThreadName:" + Thread.currentThread().getName()
                    + ";MysqLImporter.distinctDomainStats:error = " + e.getMessage());
            new Throwable(e);

        } finally {
            try {
                sqlStatement.close();
                DBHelper.closeMySQLConn(connSQL);

            } catch (SQLException e) {
                logger.error("ThreadName:" + Thread.currentThread().getName()
                        + ";MysqLImporter.distinctDomainStats:error = " + e.getMessage());
            }
        }

        logger.info("ThreadName:" + Thread.currentThread().getName() +
                ";MysqLImporter.distinctDomainStats:all time =" + (System.currentTimeMillis() - startTime));
    }

    public static void saveMysql(RetentionStats hiveStats ,Map<String, List<StatVo>> results, int internal)
            throws SQLException{
        DBHelper.setDataSource(hiveStats.getEnvFlag());
        Connection connSQL = DBHelper.getMySQLConn();
        connSQL.setSavepoint();
        String sourceSqlStr = "";
        Object[] statsParametersArray = new Object[2];
        statsParametersArray[1] = internal;
        sourceSqlStr = SystemConfig.getProperty(hiveStats.getIndexName() + ".mysql.day.sql");

        for(Map.Entry<String, List<StatVo>> entry : results.entrySet()) {
            if (entry.getKey() == "a") {
                long startTime = System.currentTimeMillis();
                statsParametersArray[0] = "a";
                String formatSqlStr = MessageFormat.format(sourceSqlStr, statsParametersArray);
                logger.info("ThreadName:" + Thread.currentThread().getName() + " " + formatSqlStr);
                PreparedStatement sqlStatement = connSQL.prepareStatement(formatSqlStr);
                saveDayKpi(connSQL, sqlStatement, entry.getValue(), Integer.valueOf(hiveStats.getRegDate()));

            } else if (entry.getKey() == "i") {
                statsParametersArray[0] = "i";
                String formatSqlStr = MessageFormat.format(sourceSqlStr, statsParametersArray);
                logger.info("ThreadName:" + Thread.currentThread().getName() + " " + formatSqlStr);
                PreparedStatement sqlStatement = connSQL.prepareStatement(formatSqlStr);
                saveDayKpi(connSQL, sqlStatement, entry.getValue(), Integer.valueOf(hiveStats.getRegDate()));

            } else {
                statsParametersArray[0] = "w";
                String formatSqlStr = MessageFormat.format(sourceSqlStr, statsParametersArray);
                logger.info("ThreadName:" + Thread.currentThread().getName() + " " + formatSqlStr);
                PreparedStatement sqlStatement = connSQL.prepareStatement(formatSqlStr);
                saveDayKpi(connSQL, sqlStatement, entry.getValue(), Integer.valueOf(hiveStats.getRegDate()));
            }
        }
        DBHelper.closeMySQLConn(connSQL);
    }

    private static void saveDayKpi(Connection conn, PreparedStatement sqlStatement,  List<StatVo> statVos, int itime) throws SQLException{
        if(statVos.isEmpty()){
            return;
        }
        int rowCount  = 0;
        long nowTime = System.currentTimeMillis();
        try{
            for(StatVo statVo : statVos){
                rowCount++;
                sqlStatement.setString(1, statVo.getAppkey());
                sqlStatement.setInt(2, itime);
                sqlStatement.setDouble(3, statVo.getValue());
                sqlStatement.addBatch();

                if (rowCount % 2000 == 0) {
                    try {
                        conn.setAutoCommit(false);
                        sqlStatement.executeBatch();
                        conn.commit();
                        sqlStatement.clearBatch();
                        conn.setAutoCommit(true);
                        logger.debug(Thread.currentThread().getName() + " MysqLImporter.saveDayKpi:rowCount=" + rowCount
                                + "; commit time =" + (System.currentTimeMillis() - nowTime));
                        nowTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        conn.rollback();
                        logger.error(Thread.currentThread().getName() + "MysqLImporter.saveDayKpi:error = " + e.getMessage());
                    }
                    rowCount = 0;
                }
            }

            if (rowCount % 2000 != 0) {
                logger.debug(Thread.currentThread().getName() + "MysqLImporter.saveDayKpi:rowCount=" + rowCount);
                try {
                    conn.setAutoCommit(false);
                    sqlStatement.executeBatch();
                    conn.commit();
                    sqlStatement.clearBatch();
                    conn.setAutoCommit(true);
                    logger.info(Thread.currentThread().getName() + "MysqLImporter.saveDayKpi:rowCount=" + rowCount
                            + "; commit time =" + (System.currentTimeMillis() - nowTime));
                } catch (Exception e) {
                    conn.rollback();
                    logger.error(Thread.currentThread().getName() + "MysqLImporter.saveDayKpi:error = " + e.getMessage());
                }
            }
        }catch (Exception e){
            new Throwable(new SQLException(e));

        }finally {
            sqlStatement.close();
        }
    }
}
