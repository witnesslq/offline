package cn.jpush.util.producer;

import cn.jpush.mq.MQ;
import cn.jpush.mq.MQFactory;
import cn.jpush.util.SystemConfig;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.QueueingConsumer;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by fengwu on 15/5/14.
 */
public class RabbitMQProducer {

    private static final Logger LOG = Logger.getLogger(RabbitMQProducer.class);


    public static void main(String[] args) {
        Properties props = new Properties();

        String topic = args[0];
        String mqName = args[1];
        String mqHost = args[2];
        Address add = Address.parseAddress(mqHost);
        MQ mq = MQFactory.newInstance(mqName);
        QueueingConsumer consumer = mq.createConsumer(add);
        props.put("metadata.broker.list", SystemConfig.getProperty("kafka.metadata.broker.list"));
        props.put("request.required.acks", "-1");
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("producer.type", "sync");
        QueueingConsumer.Delivery delivery = null;
        BaseProducer message = new BaseProducer(props);

        while (true){
            try {
                delivery = consumer.nextDelivery(0);
                if (delivery == null) {
                    continue;
                }
                message.publish(delivery.getBody(), topic);
            } catch (Exception e) {
                LOG.error(e);
                consumer = mq.createConsumer(add);
            }
        }
        //Baseproducer.writeJsonFile(file, crashLogAvros);
        //demo.close();
    }

}
