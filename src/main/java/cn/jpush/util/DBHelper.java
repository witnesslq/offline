package cn.jpush.util;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cn.jpush.util.SystemConfig;

/**
 * @author wufeng
 * @since JDK 1.6
 */
public class DBHelper {

    private static Logger logger = LoggerFactory.getLogger(DBHelper.class);

    private static final String DRIVER_HIVE = "org.apache.hive.jdbc.HiveDriver";

    // 连接的容器
    public static ThreadLocal<Connection> container = new ThreadLocal<Connection>();

    // 定义c3p0 数据源
    private static DataSource ds;


    private DBHelper() {
    }

    public static Connection getHiveConn(String envFlag) throws InterruptedException,
            ClassNotFoundException, SQLException {
        Connection connToHive = null;
        Class<?> hiveDrive = null;
        try {
            hiveDrive = Class.forName(DRIVER_HIVE);
            connToHive = DriverManager.getConnection(SystemConfig.getProperty(envFlag + ".hive.db.url"),
                            SystemConfig.getProperty(envFlag + ".hive.db.user"),
                            SystemConfig.getProperty(envFlag + ".hive.db.password", ""));

        } catch (SQLException e) {
            int count = 0;
            while (true) {
                count++;
                Thread.sleep(5 * 60 * 1000);   //sleep 5 minutes and try connect again
                connToHive =  DriverManager.getConnection(SystemConfig.getProperty(envFlag + ".hive.db.url"),
                                SystemConfig.getProperty(envFlag + ".hive.db.user"),
                                SystemConfig.getProperty(envFlag + ".hive.db.password", ""));
                if (connToHive != null || count == 3){
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("DBHelper.getHiveConn:error = " + e.getMessage());
        }

        return connToHive;
    }

    public static Connection getMySQLConn() throws SQLException {
        Connection conn = null;
        ComboPooledDataSource cpds = (ComboPooledDataSource) getDataSource();
        conn = cpds.getConnection();
        return conn;
    }

    public static void closeHiveConn(Connection connToHive) {
        try {
            if (connToHive != null) {
                connToHive.close();
            }
        } catch (Exception e) {
            logger.error("DBHelper.closeHiveConn:error = " + e.getMessage());
        }
    }

    public static void closeMySQLConn(Connection connToMySQL) {
        try {
            if (connToMySQL != null) {
                connToMySQL.close();
            }
        } catch (Exception e) {

            logger.error("DBHelper.closeMySQLConn:error = " + e.getMessage());

        }
    }

    /*
     * 完成的功能： 1.从数据源中获取Connection 2.开启事务 3. 放到线程上
     */
    public static void startTransaction() throws SQLException {
        Connection conn = container.get();
        // 当前线程上是否已经存在连接
        if (conn == null) {
            conn = ds.getConnection();
        }
        // 开启事务
        conn.setAutoCommit(false);
        // 放到当前线程上
        container.set(conn);
    }

    // 提交当前线程上的连接
    public static void commit() throws SQLException {
        Connection conn = container.get();
        if (conn != null) {
            conn.commit();
        }
    }

    // 回滚当前线程上的连接
    public static void rollback() throws SQLException {
        Connection conn = container.get();
        if (conn != null) {
            conn.rollback();
        }
    }

    // 释放当前线程上的连接
    public static void release() throws SQLException {
        Connection conn = container.get();
        if (conn != null) {
            // 从当前线程上，拿掉连接
            container.remove();
            conn.close();
        }
    }

    // 返回数据源
    public static DataSource getDataSource() {
        return ds;
    }

    // 初始化数据源
    public static void setDataSource(String envFlag) {
        ds = new ComboPooledDataSource(SystemConfig.getProperty(envFlag + ".c3p0.mysql"));
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // 释放资源
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                rs = null;
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                st = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                conn = null;
            }
        }
    }

}
