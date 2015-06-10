package cn.jpush.stat.offline.v2.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.tool.Alarm;
import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.util.CalendarUtil;
import cn.jpush.util.DBHelper;
import cn.jpush.util.StatConstantUtil;
import cn.jpush.util.SystemConfig;
import cn.jpush.utils.statsdb.SaveStatsData;
import cn.jpush.utils.statsdb.StatVo;

import com.google.common.base.Strings;

/**
 * @author qiuyue
 */
public class StatsMain {

    private static Logger logger = LoggerFactory.getLogger(StatsMain.class);

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        //right args format

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];
        String frequency = args[4];
        String tableFlag = args[5];

        StatsMain.statsAndSave(envFlag, indexName, statsDate, frequency, tableFlag);

        logger.info(" statsMain.main: all  time =" + (System.currentTimeMillis() - startTime));
    }

    public static void statsAndSave(String envFlag, String indexName, String statsDate,
                                    String frequency, String tableFlag) throws Exception {
        try {
            logger.info("statsMain.statsAndSave:indexName=" + indexName + ";statsDate=" + statsDate
                    + ";tableType=" + frequency);

            long startTime = System.currentTimeMillis();
            String statsYear = statsDate.substring(0, 4);
            String statsMonth = statsDate.substring(0, 6);
            String statsDay = statsDate.substring(0, 8);
            String statsHour = "hour_" + statsDate.substring(8);

            Object[] statsDateArray = {statsYear, statsDate.substring(4, 6), statsDate.substring(6, 8), statsDate.substring(8)};

            String statsSql = "";
            if (StatConstantUtil.INNER_TABLE.equals(tableFlag)) {
                // incr , active
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
            if (StatConstantUtil.EXTERNAL_TABLE.equals(tableFlag)) {
                // user_online,apns_report, http_report, msg_recv
                if ("hour".equals(frequency)) {
                    statsSql = SystemConfig.getProperty(indexName + ".v2.stats.hour.sql");
                }
                if ("day".equals(frequency)) {
                    statsSql = SystemConfig.getProperty(indexName + ".v2.stats.day.sql");

                }
                statsSql = MessageFormat.format(statsSql, statsDateArray);
            }

            long nowTime = System.currentTimeMillis();

            ResultSet resultSet = null;
            Connection connHive = DBHelper.getHiveConn(envFlag);
            HiveStatement hiveStatement = (HiveStatement) connHive.createStatement();

            logger.info("statsMain.statsAndSave:statsSql=" + statsSql);

            if (!Strings.isNullOrEmpty(statsSql)) {

                resultSet = hiveStatement.executeQuery(statsSql);
            }

            logger.info("statsMain.statsAndSave:query time ="
                    + (System.currentTimeMillis() - nowTime));

            nowTime = System.currentTimeMillis();

            StatVo statVo = null;
            List<StatVo> list = new ArrayList<StatVo>();
            while (resultSet.next()) {
                String appKey = resultSet.getString("appkey");
                String platform = resultSet.getString("platform");
                long cnt = 0L;
                cnt = resultSet.getLong("cnt");
                statVo = new StatVo();
                statVo.setAppkey(appKey);
                statVo.setPlatform(platform);
                statVo.setValue(cnt);
                list.add(statVo);
            }

            String newIndexName = indexName;

            if ("user_online".equals(indexName)) {
                newIndexName = "pushonlineuser";
            } else if ("user_incr".equals(indexName)) {
                newIndexName = "pushnewuser";
            } else if ("user_active".equals(indexName)) {
                newIndexName = "pushactiveuser";
            } else if ("user_duration".equals(indexName)) {
                newIndexName = "pushusedtime";
            } else if ("user_startup".equals(indexName)) {
                newIndexName = "pushopentimes";
            } else if ("pushapns".equals(indexName)) {
                newIndexName = "pushdelivery";
            } else if ("pushmsgrecv".equals(indexName)) {
                newIndexName = "pushdelivery";
            }

            try {
                if ("hour".equals(frequency)) {
                    SaveStatsData.saveHourKPI("off", newIndexName, Integer.valueOf(statsDay), Integer.valueOf(statsDate.substring(8)), list, true);
                } else if ("day".equals(frequency)) {
                    SaveStatsData.saveKPI("off", "d", newIndexName, Integer.valueOf(statsDay), list, true);
                } else if ("month".equals(frequency)) {
                    SaveStatsData.saveKPI("off", "m", newIndexName, Integer.valueOf(statsMonth), list, true);
                }
            } catch (Exception e) {
                logger.error("statsMain.savenew db:error = " + e.getMessage());
            }

            logger.info("statsMain.statsAndSave:all time =" + (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            Alarm.alarm(64, String.format("%s user active or user reg offline stats error = %s" ,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    e.getMessage()));
            e.printStackTrace();
            logger.error("statsMain.statsAndSave:error = " + e.getMessage());
            throw new Exception(e);
        }
    }
}
