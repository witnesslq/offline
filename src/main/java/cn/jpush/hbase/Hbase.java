package cn.jpush.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Hbase {
	/*
	 * HTable Definition
	 */
	private static final Logger LOG = Logger.getLogger(Hbase.class);

	public static HConnection conn;

	public static Configuration createConf() {
		Configuration conf = HBaseConfiguration.create();
		return conf;
	}

	public synchronized static HTableInterface getTable(String tableName) {
		HTableInterface table = null;
		try {
			if (conn == null)
				conn = HConnectionManager.createConnection(createConf());
			table = conn.getTable(tableName);
			table.setAutoFlushTo(false);
		} catch (IOException e) {
			LOG.error(e);
		}
		return table;
	}

	public synchronized static boolean Reconnect() {
		try {
			if (conn != null)
				conn.close();
		} catch (IOException e) {
			LOG.error(e + ":fail to close connection");
		}
		try {
			conn = HConnectionManager.createConnection(createConf());
		} catch (IOException e) {
			LOG.error(e + ":fail to create connection");
			return false;
		}
		return true;
	}
}
