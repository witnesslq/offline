package cn.jpush.stat.offline.v2.prepare;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Created by fengwu on 15/3/27.
 */
public class AddTablePartition {

    private static Logger logger = LoggerFactory.getLogger(AddTablePartition.class);

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String envFlag = args[0];
        String statsDate = args[1];
        String indexName = args[2];
        String location = args[3];

        HiveUtil.execute(envFlag, MessageFormat.format(SystemConfig.getProperty(indexName + ".v2.prepare"),
                new Object[]{statsDate.substring(0, 4), statsDate.substring(4, 6), statsDate.substring(6, 8), location}));

        logger.info(" AddTablePartition.main: all  time =" + (System.currentTimeMillis() - startTime));
    }
}
