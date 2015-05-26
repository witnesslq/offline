/**
 * Project Name:offline
 * File Name:BusinessStatisDao.java
 * Package Name:dao
 * Date:2014年10月21日下午1:49:49
 * Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 *
*/

package cn.jpush.stat.offline.v1.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.jpush.stat.offline.v2.entity.Statistic;

/**
 * ClassName:BusinessStatisDao <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年10月21日 下午1:49:49 <br/>
 * @author   wufeng
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */
public interface BusinessStatisDao {
    
    public void create (String key, Object[] initFormatArgs) throws Throwable;
    
    public ResultSet select(String key, Object[] initFormatArgs) throws Throwable;
    
    public void delete(String sql) throws SQLException;
    
    public void insert(String key,Statistic stats, ResultSet res) throws Throwable;
    
    public void dropView(String viewName) throws SQLException, Throwable;
    
}

