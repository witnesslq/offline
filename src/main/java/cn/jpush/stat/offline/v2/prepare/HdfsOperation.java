package cn.jpush.stat.offline.v2.prepare;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fengwu on 15/4/1.
 */
public class HdfsOperation {
    private static Logger logger = LoggerFactory.getLogger(HdfsOperation.class);

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        String envFlag = args[0];
        String operation = args[1];

        HiveUtil.execute(envFlag, operation);

        logger.info(" HdfsOperation.main: all  time =" + (System.currentTimeMillis() - startTime));

    }

}
