package cn.jpush.stat.offline.v2.prepare;


import cn.jpush.util.StatConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;

import java.text.MessageFormat;


/**
 * @author qiuyue
 * 
 */
public class PrepareMain {

    private static Logger logger = LoggerFactory.getLogger(PrepareMain.class);

    public static void main(String[] args) throws Exception {
    	// TODO Auto-generated method stub
    	//right args format
    	//args[0] = reRun[false,true]
    	//args[1] = evnFlag[test,product]
    	//args[2] = Excute Time[BeiJing]
    	//args[3] = indexName 
    	//args[4] = frequency[hour,day,month]

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];
        String tableFlag = args[4];
        String location = args[5];


        if(StatConstantUtil.INNER_TABLE.equals(tableFlag)){

            HiveUtil.execute(envFlag, SystemConfig.getProperty(indexName + ".v2.prepare.tmp.prefix")
                    + statsDate + SystemConfig.getProperty(indexName + ".v2.prepare.tmp.postfix"));
        }

        HiveUtil.execute(envFlag, MessageFormat.format(SystemConfig.getProperty(indexName + ".v2.prepare"),
                new Object[]{statsDate.substring(0,4), statsDate.substring(4, 6), statsDate.substring(6, 8), statsDate, location}));


        logger.info(" PrepareMain.main: all  time =" + (System.currentTimeMillis() - startTime));

    }

}
