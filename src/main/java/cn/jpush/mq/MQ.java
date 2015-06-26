package cn.jpush.mq;

import cn.jpush.tool.Alarm;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class MQ {

	private static final Logger LOG = Logger.getLogger(MQ.class);
	private static final Properties PROPERTIES = new Properties();
	private static Connection conn = null;
	private final String name;

	/*
	 * get properties for the connection to rabbitMQ of message
	 */
	static {
		try {
			PROPERTIES.load(MQ.class.getClassLoader().getResourceAsStream(
					"RabbitMQ.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MQ(String name) {
		this.name = name;
	}
	
	public Connection createConnection(Address[] add) throws IOException {
	  if (conn == null) {
		ConnectionFactory fac = new ConnectionFactory();
		fac.setUsername(getProperty("user"));
		fac.setPassword(getProperty("password"));
		conn = fac.newConnection(add);
	  }
		return conn;
	}

	public Connection createConnection(Address add) throws IOException {
		return createConnection(new Address[] { add });
	}

	public Connection createConnection() throws IOException {
		return createConnection(Address.parseAddresses(getProperty("host")));
	}

	public Connection[] createConnections() throws IOException {
		Address[] adds = Address.parseAddresses(getProperty("host"));
		Connection[] chans = new Connection[adds.length];

		for (int i = 0; i != adds.length; i++)
			chans[i] = createConnection(adds[i]);

		return chans;
	}

	/**
	 * public message
	 * 
	 * @throws IOException
	 * 
	 */
	public void basicPublish(BasicProperties pro, String message)
			throws IOException {
		Connection conn = createConnection();
		Channel chan = conn.createChannel();
		chan.exchangeDeclare(getProperty("exchange"), "direct");
		chan.queueDeclare(getProperty("queue"), Boolean.parseBoolean(getProperty("durable")), Boolean.parseBoolean(getProperty("exclusive")),
				Boolean.parseBoolean(getProperty("autoDelete")), getArguments());
		chan.queueBind(getProperty("queue"), getProperty("exchange"), getProperty("route"));
		chan.basicPublish(getProperty("exchange"), getProperty("route"), pro,
				message.getBytes());
		chan.close();
		conn.close();
	}

	/**
	 * get Consumer from opt address
	 */
	public QueueingConsumer createConsumer(Address[] add) {
		QueueingConsumer consumer = null;
		try {
			Channel chan = createConnection(add).createChannel();
			String queueName = chan.queueDeclare(getProperty("queue"),
					Boolean.parseBoolean(getProperty("durable")),
					Boolean.parseBoolean(getProperty("exclusive")), 
					Boolean.parseBoolean(getProperty("autoDelete")), 
					getArguments()).getQueue();
			chan.queueBind(queueName, getProperty("exchange"), getProperty("route"));
			consumer = new QueueingConsumer(chan);
			chan.basicConsume(queueName, Boolean.parseBoolean(getProperty("autoAck")), consumer);
			System.out.println(String.format("connection rabbit mq host[%s]",
					chan.getConnection()));
		} catch (IOException e) {
			LOG.error("fail to create consumer");
		}
		return consumer;
	}

	/**
	 * get Consumer from single address
	 */
	public QueueingConsumer createConsumer(Address add) {
		return createConsumer(new Address[] { add });
	}

	/**
	 * the hosts is opt
	 */
	public QueueingConsumer createConsumer() {
		return createConsumer(Address.parseAddresses(getProperty("host")));
	}

	/**
	 * all of the hosts is necessary
	 */
	public QueueingConsumer[] createConsumers() {
		Address[] adds = Address.parseAddresses(getProperty("host"));
		QueueingConsumer[] consumers = new QueueingConsumer[adds.length];

		for (int i = 0; i != adds.length; i++)
			consumers[i] = createConsumer(adds[i]);

		return consumers;
	}

	/*
	 * function for getting value of properties
	 */
	
	public String getProperty(String property) {
		String pName = name + "." + property;
		return (String)(PROPERTIES.get(pName) == null ? PROPERTIES.get(property): 
			PROPERTIES.get(pName));
		
	}
	
	public Map<String, Object> getArguments() {
		return null;
	}

	/*
	 * clear the conn, espically called when conn is closed expectedly.
	 */
	public void clearConn() {
	  conn = null;
	}
	
	/**
	 * parse the record from rabbitMQ and get action
	 * 
	 * @param content
	 *            the properties of record 
	 * @return the Row
	 */
//	protected abstract Mutation getRow(Map<String, Object> content);
//
//	/**
//	 * 
//	 * @param name
//	 */
//	@SuppressWarnings("unchecked")
//	public void writeToHTable(Address address, String tableName) {
//		String tag = Thread.currentThread().getName();
//
//		// create consumer of rabbitMQ
//		QueueingConsumer consumer = createConsumer(address);
//		for (int i = 1; consumer == null; i++) {
//			LOG.info(tag + "have failed to create consumer " + i + " times");
//			try {
//				Thread.sleep(60000);
//			} catch (InterruptedException e) {
//			}
//			consumer = createConsumer(address);
//		}
//
//		// open table
//		HTableInterface table = Hbase.getTable(tableName);
//		if (table == null) {
//			LOG.error(tag + " fail to open table");
//			System.exit(-1);
//		}
//		table.setAutoFlushTo(false);
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//		
//		ArrayList<Row> rows = new ArrayList<Row>();
//		String record = null;
//		Mutation mutation = null;
//		long size = 0;
//		while (true) {
//			try {
//				record = Bytes.toString(consumer.nextDelivery().getBody());
//			} catch (Exception e) {
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e1) {
//				}
//				LOG.fatal(new Date() + ":\t reconnecting as " + e);
//				consumer = createConsumer(address);
//				continue;
//			}
//			
//			try {
//				if (record == null) {
//					Thread.sleep(3000);
//					continue;
//				}
//				mutation = getRow(mapper.readValue(record, Map.class));
//				if(mutation instanceof Put){
//					table.put((Put)mutation);
//				}else{
//					rows.add(mutation);
//					size+=mutation.heapSize();
//					if(size >= 2097152){
//						table.batch(rows);
//						size = 0;
//					}
//				}
//			} catch(Exception e) {
//				LOG.error(record);
//			}
//		}
//	}

}
