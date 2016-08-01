package spark

import java.sql.{ResultSet, DriverManager}




/**
 * Created by fengwu on 15/3/4.
 */
object dataFormat extends App {

  val sparkConf = new SparkConf().setMaster("local").setAppName("DataFormat")
  val sc = new SparkContext(sparkConf)
  // Loading text files
  val input = sc.textFile("file:///home/fengwu/Library/")

  // Loading json
  case class Person(name: String, lovePandas: Boolean)

  val mapper = new ObjectMapper()
  val result = input.flatMap(record => {
    try {
      Some(mapper.readValue(record, classOf[Person]))
    } catch {
      case e: Exception => None
    }
  })

  //loading a sequence file
  val data = sc.sequenceFile("inFilePath", classOf[Text], classOf[IntWritable]).
    map {
    case (x, y) => (x.toString, y.get())
  }
  //data.saveAsSequenceFile()
  
  //Creating a HiveContext and selecting data
  val hiveCtx =  new HiveContext(sc)
  
  // jdbcRDD ,for mysql database
  def createConnection() ={
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    DriverManager.getConnection("jdbc:mysql://gwml.jpushoa.com:3306/developerdb?user=develop")
    
  }
  
  def extractValues(r : ResultSet) = {
    (r.getInt(1), r.getString(2))
  }
  
  val jdbcdata = new JdbcRDD(sc, createConnection, "sql",
    lowerBound = 1, upperBound = 3, numPartitions = 2, mapRow = extractValues)
  
  // Reading from HBase
  val hbConf = HBaseConfiguration.create()
  hbConf.set(TableInputFormat.INPUT_TABLE, "table name") // which table to scan
  
  val hbaseRdd = sc.newAPIHadoopRDD(hbConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
  
}
