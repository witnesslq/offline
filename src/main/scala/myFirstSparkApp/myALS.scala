package myFirstSparkApp

import org.apache.spark._
import org.apache.spark.mllib.recommendation._

/**
 * Created by fengwu on 15/2/5.
 */
object myALS {

  def showWarning() {
    System.err.println(
      """WARN: This is a naive implementation of ALS and is given as an example!
        |Please use the ALS method found in org.apache.spark.mllib.recommendation
        |for more conventional use.
      """.stripMargin)
  }

  def main(args: Array[String]) {

    showWarning()
    val sparkConf = new SparkConf().setMaster("local").setAppName("SparkALS")
    val sc = new SparkContext(sparkConf)

    val data = sc.textFile("src/main/Resources/ALS/test.data")
    val ratings = data.map(_.split(',') match {
      case Array(user, item, rate) =>
        Rating(user.toInt, item.toInt, rate.toDouble)
    })
    // Build the recommendation model using ALS
    val model = ALS.train(ratings, 1, 10, 0.01)
    // Evaluate the model on rating data
    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }


    val predictions = model.predict(usersProducts)
    println("" + predictions.count())
    predictions.map(x => "user-" + x.user + " product-" + x.product + " rating->" + x.rating).foreach(println(_))
    //println(predictions.toArray().toList)
  }
}
