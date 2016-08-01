package exercises

import scala.io.Source
import java.util.Scanner

/**
 * Created by fengwu on 15/1/27.
 */
object Chapter4 extends App {
  //exercise 1
  //  val prices = Map("book" -> 10.0, "pen" -> 5.2, "eraser" -> 3.0)
  //
  //  var prices2 = for ((k, v) <- prices) yield k -> (v * 0.9)
  //
  //  println(prices)
  //  println(prices2)

  //exercise 2
  def readFile(filePath: String): Unit = {
    val source = Source.fromFile(filePath, "UTF-8")

    for (l <- source.getLines) println(l.mkString)
    source.close()
  }

  // exercise 3
  def wordCount(filePath: String) :collection.mutable.Map[String, Int] = {
    val in = new Scanner(new java.io.File("/Users/fengwu/Applications/百度云同步盘/settings.xml"))
    var wordCounts = collection.mutable.Map[String, Int]() withDefault(_ => 0)
    while (in hasNext) wordCounts(in next) += 1
    wordCounts
  }

  // exercise 7
  def displayJavaProps {
    val props = collection.JavaConversions.propertiesAsScalaMap(System.getProperties)
    val maxLengthKey = ((props.keySet).toList) maxBy(_ size)
    val maxLengthkey = props.keySet.map(_.length).max
    for ((k, v) <- props) println( k + " " * (maxLengthKey.size - k.size) + " | " + v)
  }

  //println(wordCount("/Users/fengwu/Applications/百度云同步盘/settings.xml"))
  //displayJavaProps

  // exercise 8
  def minmax(values: Array[Int]) = {
    (values.min, values.max) // Tuple
  }

  //println(minmax(Array(-5, 0, 5, 9, -2, 17, 3)).getClass)
  def lteqgt(values : Array[Int], v : Int) : Tuple3[Int, Int, Int] = {
    (values.filter( _ > v).size, values.filter(_ == v).size, values.filter(_ < v).size)
  }

  println(lteqgt(Array(-5, 0, 5, 9, -2, 17, 3), 0))

}
