package cn.jpush.stat.offline.v2.prepare;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Created by fengwu on 15/4/1.
 */
public class DynamicPartition {

    private static Logger logger = LoggerFactory.getLogger(DynamicPartition.class);

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String envFlag = args[0];
        String statsDate = args[1];
        String indexName = args[2];
        // meta data load into hive tmp table
        // data repartition
        // restore meta data

        String repartitionSql =  MessageFormat.format(SystemConfig.getProperty(indexName + ".v2.repartition"),
                new Object[]{statsDate.substring(0, 8)});

        HiveUtil.execute(envFlag, repartitionSql);


        logger.info(" DynamicPartition.main: all  time =" + (System.currentTimeMillis() - startTime));
    }
}
