/**
 * Project Name:offline File Name:Business.java Package Name:cn.jpush.stat.offline.main
 * Date:2014年10月21日下午1:18:55 Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 * 
 */

package cn.jpush.stat.offline.v1.main;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.stat.offline.v1.dao.BusinessStatisDao;
import cn.jpush.stat.offline.v1.dao.impl.BusinessStatisDaoImpl;
import cn.jpush.stat.offline.v1.processor.StatisProcessor;
import cn.jpush.stat.offline.v1.processor.impl.BusinessStatisProcessor;
import cn.jpush.stat.offline.v2.entity.Statistic;
/**
 * ClassName:Business <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2014年10月21日 下午1:18:55 <br/>
 * 
 * @author wufeng
 * @version
 * @since JDK 1.7
 * @see
 */
public class Business {
    private static Logger logger = LoggerFactory.getLogger(Business.class);

    public static void main(String[] args) throws Throwable, SQLException {
    
            String envFlag = args[0];
            String statsDate = args[1];
            String statsType = args[2];  //activeuser, opentimes, usedtime
            String platform = args[3];
            String frequency = args[4];
            
            Statistic stats = new Statistic(statsDate, statsType, platform, frequency);
                       
            BusinessStatisDao dao = new BusinessStatisDaoImpl(envFlag);
            StatisProcessor processor = new BusinessStatisProcessor(stats, dao);
            processor.run();                   
    }
}
