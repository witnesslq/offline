package cn.jpush.mq;

import com.rabbitmq.client.Address;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.log4j.Logger;
import cn.jpush.hbase.Hbase;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MQFactory {
	private static final Logger LOG = Logger.getLogger(MQFactory.class);
	private static final HashMap<String, MQ> POOL = new HashMap<String, MQ>();

	public static MQ newInstance(String name) {
		MQ instance = POOL.get(name);
		if (instance == null) {
			instance = new MQ(name);
			POOL.put(name, instance);
		}
		return instance;
	}

	public static void putByMulHosts(int nThreads,String name,
			Class<? extends Runnable> threadC) throws Exception {
		Constructor<? extends Runnable> cons = threadC
				.getConstructor(Address.class);
		MQ mq = MQFactory.newInstance(name);
		Address[] adds = Address.parseAddresses(mq.getProperty("host"));
		int nAdds = adds.length;
		ThreadPoolExecutor[] execs = new ThreadPoolExecutor[nAdds];

		for (int j = 0; j != nAdds; j++) {
			execs[j] = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(nThreads);
			for (int i = 0; i != nThreads; i++)
				execs[j].execute(cons.newInstance(adds[j]));
		}

		while (true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				LOG.error(e);
			}

			for (int j = 0; j != nAdds; j++) {
				int active = execs[j].getActiveCount();
				if (active == nThreads)
					continue;

				int nLost = nThreads - active;
				LOG.fatal("pool" + j + " lost " + nLost + " consumers");
				for (int i = 0; i != nLost; i++)
					execs[j].execute(cons.newInstance(adds[j]));
			}
		}
	}

	public static void putBySingleHost(int nThreads, Address add,
			Class<? extends Runnable> threadC) throws Exception {

		Constructor<? extends Runnable> cons = threadC
				.getConstructor(Address.class);
		ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(nThreads);
		for (int i = 0; i != nThreads; i++)
			exec.execute(cons.newInstance(add));

		while (true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				LOG.error(e);
			}

			int active = exec.getActiveCount();
			if (active == nThreads)
				continue;

			int nLost = nThreads - active;
			LOG.fatal(" lost " + nLost + " consumers");
			for (int i = 0; i != nLost; i++)
				exec.execute(cons.newInstance(add));
		}
	}
	
	public static void put(String table, String name) {
		HTableInterface activeUser = Hbase.getTable(table);
		activeUser.setAutoFlushTo(false);
		
		MQ mq = newInstance(name);
	}
}
