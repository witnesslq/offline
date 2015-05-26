package cn.jpush.util;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by fengwu on 15/5/22.
 */
public class ConsumerContext {
    private String topic;
    private String groupId;
    private String className;
    private String localPath;
    private String hdfsPath;
    private boolean isMerge;
    private int thredNums;
    private ConsumerConnector consumer;

    private List<Runnable> handles;

    public ConsumerContext() {
    }

    public ConsumerContext(String topic, String groupId, String className, String localPath, String hdfsPath) {
        this.topic = topic;
        this.groupId = groupId;
        this.className = className;
        this.localPath = localPath;
        this.hdfsPath = hdfsPath;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setIsMerge(boolean isMerge) {
        this.isMerge = isMerge;
    }

    public int getThredNums() {
        return thredNums;
    }

    public void setThredNums(int thredNums) {
        this.thredNums = thredNums;
    }

    public ConsumerConnector getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerConnector consumer) {
        this.consumer = consumer;
    }

    public List<Runnable> getHandles() {
        return handles;
    }

    public void setHandles(List<Runnable> handles) {
        this.handles = handles;
    }
}
