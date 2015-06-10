package cn.jpush

import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
/**
 * Created by fengwu on 15/5/27.
 */
class CustomerClickTimesStats {


    def main(args: Array[String]) {
        // sc is an existing SparkContext
//        val statsDate = args(0)
//        val sc = new SparkContext()
//        val sqlContext = new org.apache.spark.sql.SQLContext(sc)
//        val hiveContext = new HiveContext(sc)
//        val hourSql: String = "select count(*) as cnt, appkey, platform from user_click_repartition where year = %s and month= %s and day= %s and hour= %s and click_type=0 group by appkey, platform"
//        hourSql.format(statsDate.substring(0,4), statsDate.substring(4,6),
//            statsDate.substring(6,8), statsDate.substring(8))
//        val customClick = hiveContext.sql(hourSql)
//
//        val customClickA = customClick.filter("platform = 'a'")
//        val customClickI = customClick.filter("platform = 'i'")
//        val customClickW = customClick.filter("platform = 'w'")
//
//        // conn mysql
//        val url = "jdbc:mysql://192.168.248.204:3306/statsdb"
//
//        val jdbcDF = sqlContext.load("jdbc", Map(
//            "url" -> "jdbc:mysql://192.168.248.204:3306/statsdb",
//            "dbtable" -> "t_off_d_pushcustomclick_a",
//            "driver" -> "com.mysql.jdbc.Driver",
//            "user" -> "stats_dev",
//            "password" -> "Stats_Dev15"))
//
//        val urlTest = "jdbc:mysql://114.119.7.196:3306/developerdb"
//
//        val jdbcTestDF = sqlContext.load("jdbc", Map(
//            "url" -> "jdbc:mysql://114.119.7.196:3306/developerdb",
//            "dbtable" -> "people",
//            "driver" -> "com.mysql.jdbc.Driver",
//            "user" -> "developer_root",
//            "password" -> "Developer_253"))
//
//
//
//        jdbcDF.registerTempTable("t_off_d_pushcustomclick_a")

    }

}
