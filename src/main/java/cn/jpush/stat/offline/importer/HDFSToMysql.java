package cn.jpush.stat.offline.importer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;

/**
 * Created by fengwu on 15/4/17.
 */
public class HDFSToMysql implements Tool{

    public static final String RETENTION_HDFS_PATH = "/user/log/hive/retention/";

    private Configuration conf;

    public int run(String[] strings) throws Exception {
        return 0;
    }

    public void setConf(Configuration configuration) {

    }

    public Configuration getConf() {
        return null;
    }
}
