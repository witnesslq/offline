package cn.jpush.stat.offline.v2.load;

import cn.jpush.hbase.Hbase;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.util.HiveUtil;
import cn.jpush.util.SystemConfig;

import java.io.IOException;

/**
 * @author qiuyue
 */
public class LoadMain {

    private static Logger logger = LoggerFactory.getLogger(LoadMain.class);

    private static boolean valideFileIsCompleted(String uri) throws IOException, InterruptedException {
        boolean copyCompleted = false;
        FileSystem fs = FileSystem.get(Hbase.createConf());
        Path file = new Path(uri);
        FileStatus status = fs.getFileStatus(file);
        long currFileModificationTime = status.getModificationTime();
        Thread.sleep(2 * 60 * 1000);
        long nextFileModificationTime = status.getModificationTime();
        if (0L == (nextFileModificationTime - currFileModificationTime)) {
            copyCompleted = true;
        } else {
            currFileModificationTime = nextFileModificationTime;
            Thread.sleep(5 * 60 * 1000);
            nextFileModificationTime = status.getModificationTime();
            if (0L == (nextFileModificationTime - currFileModificationTime)) {
                copyCompleted = true;
            }else{
                throw new IOException("hdfs://nameservice1/" + uri + " upload failed please try upload manually ");
            }
        }
        return copyCompleted;
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        //right args format
        //args[0] = reRun[false,true]
        //args[1] = evnFlag[test,product]
        //args[2] = Excute Time[BeiJing]
        //args[3] = indexName
        //args[4] = frequency[hour,day,month]

        long startTime = System.currentTimeMillis();
        String reRun = args[0];
        String envFlag = args[1];
        String statsDate = args[2];
        String indexName = args[3];
        String filePath = SystemConfig.getProperty(indexName + ".v2.hdfs.dir") + statsDate;

        try {
            // 获取要mv 的active文件的信息，确保已由其它线程完全写完
            boolean copyCompleted = valideFileIsCompleted(filePath);
            if (copyCompleted) {
                HiveUtil.execute(envFlag, SystemConfig.getProperty(indexName + ".v2.load.prefix") + filePath + SystemConfig.getProperty(indexName + ".v2.load.postfix") + statsDate);
                logger.info("LoadMain.main:load time =" + (System.currentTimeMillis() - startTime));
                long nowTime = System.currentTimeMillis();
                HiveUtil.execute(envFlag, SystemConfig.getProperty(indexName + ".v2.load.insert") + statsDate);
                logger.info("LoadMain.main:insert time =" + (System.currentTimeMillis() - nowTime));
            }

        } catch (Exception e) {
            if ("true".equals(reRun)) {
                if (e.getMessage().indexOf("Invalid path") < 0) {
                    throw new Exception(e);
                }
            } else {
                throw new Exception(e);
            }
        }

        logger.info("LoadMain.main:all  time =" + (System.currentTimeMillis() - startTime));
    }

}
