/**
 * Project Name:offline File Name:BusinessStatisProcessor.java Package
 * Name:cn.jpush.stat.offline.main Date:2014年10月21日下午1:23:02 Copyright (c) 2014, wufeng@jpush.cn All
 * Rights Reserved.
 * 
 */

package cn.jpush.stat.offline.v1.processor.impl;

import static cn.jpush.util.StatConstantUtil.ACTIVEUSER;
import static cn.jpush.util.StatConstantUtil.TABLE_NAME_INFIX;
import static cn.jpush.util.StatConstantUtil.TABLE_NAME_PREFIX;
import static cn.jpush.util.StatConstantUtil.AVG_STATIS_BUSINESS_VIEW_PREFIX;
import static cn.jpush.util.StatConstantUtil.APPKEY_CATEGORY_VIEW_PREFIX;
import static cn.jpush.util.StatConstantUtil.AVG_STATIS_APPKEY_VIEW_PREFIX;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.stat.offline.v1.dao.BusinessStatisDao;
import cn.jpush.stat.offline.v1.processor.StatisProcessor;
import cn.jpush.stat.offline.v2.entity.Statistic;
import cn.jpush.util.DBHelper;
import cn.jpush.util.StatConstantUtil;

/**
 * ClassName:BusinessStatisProcessor <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2014年10月21日 下午1:23:02 <br/>
 * 
 * @author wufeng
 * @version
 * @since JDK 1.7
 * @see
 */
public class BusinessStatisProcessor implements StatisProcessor {

    private Statistic stats;
       
    private BusinessStatisDao dao;
    
    private static Object[] initFormatArgs;
    
    private String[] viewNameArray;
    
    private static Logger logger = LoggerFactory.getLogger(BusinessStatisProcessor.class);

    public BusinessStatisProcessor(Statistic stats, BusinessStatisDao dao) {
        this.stats = stats;
        this.dao = dao;
    }

    private void init() {
        String statsDate = stats.getStatsDate();
        String statsType = stats.getStatsType();  //activeuser, opentimes, usedtime
        String platform = stats.getPlatform();
        String frequency = stats.getFrequency();
        
        String statisAppKeyCategoryView = new StringBuffer(APPKEY_CATEGORY_VIEW_PREFIX)
            .append(statsType).append("_").append(platform).append("_").append(statsDate).toString();              
        String appkeyAvgStatisView = new StringBuffer(AVG_STATIS_APPKEY_VIEW_PREFIX)
            .append(statsType).append("_").append(platform).append("_").append(statsDate).toString();
        String businessAvgStatisView = new StringBuffer(AVG_STATIS_BUSINESS_VIEW_PREFIX)
            .append(statsType).append("_").append(platform).append("_").append(statsDate).toString();
        
        String statisTable = new StringBuffer(TABLE_NAME_PREFIX).append(frequency).append("_")
                .append(TABLE_NAME_INFIX).append(statsType).append("_").append(platform).toString();
        
        // 保存每个appkey对应的活跃用户数
        String activeTable = new StringBuffer(TABLE_NAME_PREFIX).append(frequency).append("_")
                .append(TABLE_NAME_INFIX).append(ACTIVEUSER).append("_").append(platform).toString();
        
        // business average 
        logger.info(String.format("%s business statis by %s and %s table" 
            ,statsType, statisTable, activeTable));
        
        String appAvg = new StringBuffer(statsType).append(StatConstantUtil.APP_AVG_POSTFIX).toString();
        
        String cidAvg = new StringBuffer(statsType).append(StatConstantUtil.BUSSINESS_AVG_POSTFIX).toString();

        Object[] initArgs = {statisAppKeyCategoryView, statisTable, statsDate,activeTable, 
                             appkeyAvgStatisView, appAvg,
                             businessAvgStatisView, cidAvg};
        
        String[] views = {statisAppKeyCategoryView, appkeyAvgStatisView, businessAvgStatisView};
        
        initFormatArgs = initArgs;
        viewNameArray = views;
    }

    public void prepare() {

    }

    public void stats() {

    }

    public void clear() throws Throwable {
        if (null != viewNameArray && viewNameArray.length != 0){
            for (int i = 0; i < viewNameArray.length; i++){
                dao.dropView(viewNameArray[i]);
            }
        }
    }

    public void run() throws Throwable ,SQLException{
        init();      
        try{
            // 清除临时视图
            clear();
            DBHelper.startTransaction();
            logger.info("business stats transaction start ");
            // 先关联出appkey对应的行业信息，然后统计出每个appkey当天的平均统计值，最后由行业下所有appkey的平均值再取平均得到行业平均
            dao.create("mysql.replace.local.appkey.cid", null);
            dao.create("mysql.create.view.appkey.cid", initFormatArgs);
            dao.create("mysql.create.view.appkey.avg.statis", initFormatArgs);
            dao.create("mysql.create.view.business.avg.statis", initFormatArgs);
            ResultSet res = dao.select("mysql.select.business.avg.statis", initFormatArgs);
            dao.insert("mysql.insert.business",stats, res);
            
            // 清除临时视图
            clear();
            DBHelper.commit();
        }catch(SQLException e){
            logger.info("bussniss stats error,db operations would be roll back,please check error logs");
            DBHelper.rollback();
            throw new Throwable(e);
        }finally{
            DBHelper.release();
            logger.info("business stats transaction end ");
        }
    }
}
