package cn.jpush.stat.offline.v2.clear;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;

/**
 * @author qiuyue
 *
 */
public class ClearMain {

	private static Logger logger = LoggerFactory.getLogger(ClearMain.class);

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
		
		HiveUtil.execute(envFlag,SystemConfig.getProperty(indexName+".v2.clear.tmp")+statsDate);
		
		logger.info("clearMain.main:all  time =" + (System.currentTimeMillis() - startTime));

	}

}
