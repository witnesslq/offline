package cn.jpush.stat.offline.udf;

import cn.jpush.utils.cache.UidInfo;
import cn.jpush.utils.cache.UidLocalCache;
import com.google.common.cache.LoadingCache;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by fengwu on 15/3/23.
 */
    public class CouchBaseUDF extends UDF {

    private static Logger Log = LoggerFactory.getLogger(CouchBaseUDF.class);

    private static UidLocalCache uc = new UidLocalCache();

    public Text evaluate(final Text input){
        if (input == null){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(input.toString()).append("\t");
        try{
            long uid = Long.parseLong(input.toString());
            UidInfo info = uc.get(uid);
            if(null != info){
                String appkey = info.getAppkey();
                String platform = info.getPlatform();
                if (null != appkey) {
                    sb.append(appkey).append("\t");
                }else{
                    sb.append("").append("\t");
                }

                if(null != platform){
                    sb.append(platform);
                }else{
                    sb.append("");
                }
            }
        }catch (NumberFormatException e){
            Log.error("can't parse this:" + input.toString());
        }
        return new Text(sb.toString());
    }
}
