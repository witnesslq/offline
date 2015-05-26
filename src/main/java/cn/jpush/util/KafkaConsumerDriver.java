package cn.jpush.util;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by fengwu on 15/4/22.
 */
public class KafkaConsumerDriver {

    protected static final Logger LOG = Logger.getLogger(KafkaConsumerDriver.class);

    private ConsumerContext context;

    private ExecutorService executor;

    public KafkaConsumerDriver(ConsumerContext context) {
        this.context = context;
    }


    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("serializer.class", SystemConfig.getProperty("kafka.serializer.class"));
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("offsets.storage", "kafka");
        props.put("dual.commit.enabled", "false");

        return new ConsumerConfig(props);
    }


    public void shutdown() {
        if (context.getConsumer() != null) context.getConsumer().shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                LOG.info("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted during shutdown, exiting uncleanly");
        }
    }


    public void drive(ConsumerContext context) {
        try {
            int a_numThreads = context.getThredNums();

            // now launch all the threads
            executor = Executors.newFixedThreadPool(a_numThreads);
            List<Runnable> runners = context.getHandles();
            for (Runnable task : runners) {
                executor.submit(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();

        Option o = new Option("t", "topic", true,
                "topic name you want to consume from kafka)");
        o.setArgName("topic-name");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("g", "group", true,
                "group id which consumer belong to)");
        o.setArgName("group-id");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("w", "writer", true,
                "real consumer class name (must exist)");
        o.setArgName("class-name:");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("n", "numbers", true,
                "consumer thread numbers ");
        o.setArgName("thread-number");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("l", "local", true,
                "the local path to store data that read from kafka");
        o.setArgName("local-path");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("h", "hdfs", true,
                "the hdfs path to store data which upload from local");
        o.setArgName("hdfs-path");
        o.setRequired(false);
        options.addOption(o);


        options.addOption("d", "debug", false, "switch on DEBUG log level");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            //formatter.printHelp(NAME + " ", options, true);
            System.exit(-1);
        }

        return cmd;
    }

    public static void main(String[] args) throws ParseException,
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        CommandLine cmd = parseArgs(args);
        int a_numThreads = Integer.parseInt(cmd.getOptionValue("n"));
        String topic = cmd.getOptionValue("t");
        String groupId = cmd.getOptionValue("g");
        String className = cmd.getOptionValue("w");

        ConsumerContext context = new ConsumerContext();
        context.setThredNums(a_numThreads);

        // initiate kafka streams context
        final ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(SystemConfig.getProperty("kafka.zookeeper.connect"), groupId));

        KafkaConsumerDriver driver = new KafkaConsumerDriver(context);

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        // now create an object to consume the messages
        // initiate consumer proxy task
        Constructor con = null;
        Class[] paramTypes = null;
        boolean merge = false;
        Class consumerImpl = Class.forName(className);
        List<Runnable> runners = new ArrayList<Runnable>();

        if (KafkaHbaseWriter.class.isAssignableFrom(consumerImpl)) {
            int threadNumber = 0;
            paramTypes = new Class[]{KafkaStream.class, int.class, boolean.class};
            con = consumerImpl.getConstructor(paramTypes);

            for (final KafkaStream stream : streams) {
                runners.add((Runnable) con.newInstance(new Object[]{stream, ++threadNumber, true}));
            }


        } else if (KafkaLocalWritter.class.isAssignableFrom(consumerImpl)) {
            int threadNumber = 0;
            paramTypes = new Class[]{KafkaStream.class, int.class, boolean.class, String.class, String.class};
            con = consumerImpl.getConstructor(paramTypes);
            String localPath = cmd.getOptionValue("l");
            String hdfsPath = cmd.getOptionValue("h");
            context.setLocalPath(localPath);
            context.setHdfsPath(hdfsPath);

            for (final KafkaStream stream : streams) {
                threadNumber++;
                System.out.println("################### current thread nums is " + threadNumber);
                if (threadNumber == a_numThreads) merge = true;
                runners.add((Runnable) con.newInstance(new Object[]{stream, threadNumber, merge, localPath, hdfsPath}));
            }
        } else {
            LOG.error(new UnsupportedClassVersionError("can't support "));
        }
        context.setHandles(runners);

        LOG.info("create kafka consumer context success ");
        driver.drive(context);
    }

}
