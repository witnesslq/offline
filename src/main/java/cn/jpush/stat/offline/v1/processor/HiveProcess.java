package cn.jpush.stat.offline.v1.processor;

public interface HiveProcess {

    public void prepare();

    public void load(String filePath, String statsDate);

    public void stats(String statsDate);

    public void clear();

    public void run();
}
