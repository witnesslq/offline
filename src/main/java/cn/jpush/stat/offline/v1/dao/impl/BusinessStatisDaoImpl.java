/**
 * Project Name:offline File Name:BusinessStatisDaoImpl.java Package Name:dao.impl
 * Date:2014年10月21日下午1:58:28 Copyright (c) 2014, wufeng@jpush.cn All Rights Reserved.
 * 
 */

package cn.jpush.stat.offline.v1.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.stat.offline.v1.dao.BusinessStatisDao;
import cn.jpush.stat.offline.v2.entity.Statistic;
import cn.jpush.util.DBHelper;
import cn.jpush.util.SystemConfig;

/**
 * ClassName:BusinessStatisDaoImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2014年10月21日 下午1:58:28 <br/>
 * 
 * @author wufeng
 * @version
 * @since JDK 1.7
 * @see
 */
public class BusinessStatisDaoImpl implements BusinessStatisDao {

    private static Logger logger = LoggerFactory.getLogger(BusinessStatisDaoImpl.class);

    public BusinessStatisDaoImpl(String envFlag) {
        DBHelper.setDataSource(envFlag);
    }

    public void create(String key, Object[] args) throws Throwable {
        PreparedStatement sqlStatement = null;
        Connection conn = null;
        try {
            conn = DBHelper.getMySQLConn();
            String sql = initSql(key, args);
            sqlStatement = conn.prepareStatement(sql);
            sqlStatement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Throwable(e.getMessage());
        } finally {
            DBHelper.release(conn,sqlStatement, null); 
        }
    }

    public void insert(String key, Statistic stats, ResultSet res) throws SQLException {
        Connection conn = null;
        PreparedStatement sqlStatement = null;
        try{
            conn = DBHelper.getMySQLConn();
            String sql = SystemConfig.getProperty(key);
            sqlStatement = conn.prepareStatement(sql);
            while (res.next()) {
                int cid = res.getInt("cid");
                int itime = res.getInt("itime");
                float avg = res.getFloat(3);
                sqlStatement.setInt(1, cid);
                sqlStatement.setInt(2, itime);
                sqlStatement.setString(3, stats.getPlatform());
                sqlStatement.setString(4, stats.getStatsType());
                sqlStatement.setFloat(5, avg);
                sqlStatement.execute();
            }
        }catch(SQLException e){
            logger.error("insert bussiness stats result error: " + e.getMessage());
            throw new SQLException(e);
        }finally {
            DBHelper.release(conn,sqlStatement, res);      
        }
    }

    public void delete(String sql) {

        // TODO Auto-generated method stub

    }

    public void dropView(String viewName) throws Throwable {
        PreparedStatement sqlStatement = null;
        Connection conn = null;
        try {
            conn = DBHelper.getMySQLConn();
            Object[] args = {viewName};
            String sql = initSql("mysql.drop.view", args);
            sqlStatement = conn.prepareStatement(sql);
            sqlStatement.execute(sql);

        } catch (SQLException e) {
            logger.error("bussiness stat drop temp view error: " + e.getMessage());
            new Throwable(e.getMessage());
        } finally {
            DBHelper.release(conn,sqlStatement, null);
        }
    }

    private String initSql(String key, Object[] args) {

        String format = SystemConfig.getProperty(key);

        return args == null ? format : MessageFormat.format(format, args);
    }

    public ResultSet select(String key, Object[] initFormatArgs) throws Throwable {

        PreparedStatement sqlStatement = null;
        ResultSet res = null;
        String sql = null;
        Connection conn = null;
        try {
            conn = DBHelper.getMySQLConn();
            sql = initSql(key, initFormatArgs);
            sqlStatement = conn.prepareStatement(sql);
            res = sqlStatement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            new Throwable(e.getMessage());
        }
        return res;
    }
}
