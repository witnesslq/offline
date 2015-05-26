package cn.jpush.mq;

import cn.jpush.hbase.Hbase;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.QueueingConsumer;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * used to write from rabbitMQ queue to HTable
 * 
 * @author wangxb
 */
public class HBaseWriter {
	private static final Logger LOG = Logger.getLogger(HBaseWriter.class);

	private String mqName;
	private String tableName;

	/**
	 * the only constructor
	 * 
	 * @param mq
	 *            subClass of MQ
	 * @param table
	 *            name of HTable
	 */
	public HBaseWriter(String mq, String table) {
		mqName = mq;
		tableName = table;
	}

	/**
	 * continuous getting record from rabbitMQ,then parsing and putting
	 *
	 *  the address of rabbitMQ server
	 */
	public void run(Address address) {
		String tag = Thread.currentThread().getName();

		// create consumer of rabbitMQ
		MQ mq = MQFactory.newInstance(mqName);
		QueueingConsumer consumer = mq.createConsumer(address);
		for (int i = 1; consumer == null; i++) {
			LOG.info(tag + "have failed to create consumer " + i + " times");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
			consumer = mq.createConsumer(address);
		}

		// open table
		HTableInterface table = Hbase.getTable(tableName);
		if (table == null) {
			LOG.error(tag + " fail to open table");
			System.exit(-1);
		}
		table.setAutoFlushTo(false);
		
		//ObjectMapper to parse JSON object
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		
		while(true){
			
		}
	}
}
