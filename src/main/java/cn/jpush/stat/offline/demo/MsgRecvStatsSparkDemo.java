package cn.jpush.stat.offline.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import scala.Tuple3;

/**
 * Created by fengwu on 15/5/25.
 */
public class MsgRecvStatsSparkDemo {


    static class MsgRecvWithApp implements Function<String, String>{
        @Override
        public String call(String s) throws Exception {
            return null;
        }
    }

    public static void main(String[] args) {
        String inputHdfsPath = args[0];
        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> msgRecvs = sc.textFile(inputHdfsPath);

        msgRecvs.distinct();

        PairFunction<String, String, String> keyData = new PairFunction<String, String, String>() {
            @Override
            public Tuple2<String, String> call(String x) {
                return new Tuple2(x.split("\t")[0], x);
            }
        };

        JavaPairRDD<String, String> pairsUnique = msgRecvs.mapToPair(keyData).distinct(); // <uid, value>


        

        JavaPairRDD<String, Iterable<String>> uidGroups =  msgRecvs.groupBy(new Function<String, String>() {
            @Override
            public String call(String s) throws Exception {
                return null;
            }
        });



    }
}
