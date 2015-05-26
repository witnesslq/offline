package cn.jpush.stat.offline.v2.prepare;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fengwu on 15/3/28.
 */
public class CreateTable {

    private static Logger logger = LoggerFactory.getLogger(CreateTable.class);

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];

        HiveUtil.execute(envFlag, SystemConfig.getProperty(indexName + ".v2.prepare.tmp.prefix")
                + statsDate + SystemConfig.getProperty(indexName + ".v2.prepare.tmp.postfix"));

        HiveUtil.execute(envFlag, SystemConfig.getProperty(indexName + ".v2.prepare"));

        logger.info(" PrepareMain.main: all  time =" + (System.currentTimeMillis() - startTime));

    }
}
