package cn.jpush.util;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.spark.SparkContext;
import org.apache.spark.SparkConf;


/**
 * Created by fengwu on 15/5/25.
 */
public class CouchBaseHelper {

    private static final Logger LOG = Logger.getLogger(CouchBaseHelper.class);

    private static final String CONF_CB_SERVER = "cb.server";
    private static final String CONF_CB_BUCKET = "cb.bucket";
    private static final String CONF_CB_PASSWORD = "cb.password";

    private static String cbHost;
    private static String bucket;
    private static String pwd;

    private static  CouchbaseClient cli;

    private static JsonParser jp = new JsonParser();

    private static void initCBConfig() {
        Properties cbConfig = new Properties();
        try {
            cbConfig.load(new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "/couchBase.properties")));
        } catch (Exception exp1) {
            try {
                cbConfig.load(CouchBaseHelper.class.getClassLoader().getResourceAsStream("couchBase.properties"));
                LOG.info("load default couchBase.properties!");
            } catch (Exception exp2) {
                LOG.error("couchBase.properties not exist!", exp2);
                System.exit(-1);
            }
        }

        bucket = cbConfig.getProperty("userprofile.couchbase.bucket");
        pwd = cbConfig.getProperty("userprofile.couchbase.pass");
        cbHost = cbConfig.getProperty("userprofile.couchbase.host");
        LOG.info("Couchbase IP is " + cbHost);
        LOG.info("BucketName is " + bucket);

    }

    public static CouchbaseClient getCouchBaseClient(){
        if (cli != null){
            return cli;
        }

        String[] serverNames = SystemConfig.getProperty(CONF_CB_SERVER).split(",");
        ArrayList<URI> serverList = new ArrayList<URI>();
        for (String serverName : serverNames) {
            URI base = null;
            base = URI.create(String.format("http://%s/pools", serverName));
            serverList.add(base);
        }
        String bucket = SystemConfig.getProperty(CONF_CB_BUCKET);
        String pwd = SystemConfig.getProperty(CONF_CB_PASSWORD);

        try {
            cli = new CouchbaseClient(new CouchbaseConnectionFactory(serverList, bucket, pwd));
        } catch (IOException e) {
            LOG.error("Init user cb error!", e);
            System.exit(-1);
        }

        return cli;

    }


//    private Map<String, String[]> queryCBWithBulk() {
//        Map<String, Object> result = null;
//        Map<String, String[]> reslist = new HashMap<String, String[]>();
//        String jsonStr;
//        String uid = null;
//        JsonObject jsonObj;
//        try {
//            result = cli.asyncGetBulk(hash.keySet()).get(3, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            LOG.error("query cb error", e);
//        } catch (ExecutionException e) {
//            LOG.error("query cb error", e);
//        } catch (TimeoutException e) {
//            LOG.error("query cb error", e);
//        }
//
//        Iterator<String> it = result.keySet().iterator();
//        while (it.hasNext()) {
//            uid = it.next();
//            jsonStr = (String) result.get(uid);
//            try {
//                if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
//                    jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
//                }
//                String[] arr = new String[2];
//                jsonObj = (JsonObject) jp.parse(jsonStr.replace("\\\"", "\""));
//                arr[0] = jsonObj.get("appkey").getAsString();
//                int code = jsonObj.get("platform").getAsInt();
//                switch (code) {
//                    case 0:
//                        arr[1] = "a";
//                        break;
//                    case 1:
//                        arr[1] = "i";
//                        break;
//                    case 2:
//                        arr[1] = "w";
//                        break;
//                    default:
//                        arr[1] = "a";
//                }
//                reslist.put(uid, arr);
//            } catch (Exception e) {
//                LOG.error("uid info format error, data:" + jsonStr, e);
//            }
//        }
//        return reslist;
//    }
}



