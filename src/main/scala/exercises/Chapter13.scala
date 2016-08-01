package exercises

import scala.collection.mutable._

/**
 * Created by fengwu on 15/1/31.
 */
object Chapter13 extends App {

  // exercise1 mutable
  def indexs(s: String) = {
    var res = new HashMap[Char, LinkedHashSet[Int]]()
    for ((c, i) <- s.zipWithIndex) {
      val set = res.getOrElse(c, new LinkedHashSet[Int])
      set += i
      res(c) = set
    }
    res
  }

  //exercise2 immutable
  def indexsImmutable(s: String) = {
    "Mississippi".zipWithIndex.groupBy(_._1).map(x => (x._1, x._2.map(_._2).toList))
  }

  //println(indexs("Missisipi"))

  // exercise 3
  def filterZero(lst: List[Int]): List[Int] = lst match {
    case Nil => Nil
    case h :: t => if (h != 0) h :: filterZero(t) else filterZero(t)
  }

  val x = List(5, 7, 0, 18, 22, 0, 12, 1, 0, 5, 0)
  val lst = filterZero(x)
  //println(x)
  //println(filterZero(x))

  // exercise 4
  def exsitsIn(a: Array[String], m : scala.collection.mutable.Map[String, Int]) = {
    a.map(m.get(_)).flatMap(x => x)
  }

  val a = Array("Tom", "Fred", "Harry")
  val m = Map("Tom" -> 3, "Dick" -> 4, "Harry" ->5)

  //println(exsitsIn(a , m).toList)

  // exercise 6
  println(lst)
  println((lst :\ List[Int]())(_ :: _))   // foldRight
  println((List[Int]() /: lst)(_ :+ _))   // foldLeft

  //exercise 7
  val prices = List(5.0 ,20.0, 9.95)
  val quantities = List(10, 2, 1)

  val xx = ((prices zip quantities).map(p => p._1 * p._2)).sum

  val y = ((prices zip quantities) map Function.tupled(_ * _)).sum
  val z = ((prices, quantities).zipped map( _ * _)).sum

  println(xx)
  println(y)
  println(z)


  // exercise 10
  def printMills(msg:String)(block: => Unit){
    val start = System.currentTimeMillis()
    block
    val end = System.currentTimeMillis()
    println(msg.format( end - start))
  }

  val str = scala.io.Source.fromURL("https://github.com/hempalex/scala-impatient/blob/master/Chapter13/10.txt")
  printMills("Using mutable collection: %d ms") {
    val freq = new HashMap[Char, Int]()
    for (c <- str) freq(c) = freq.getOrElse(c, 0) + 1
    println(freq.toSeq.sorted)
  }

  printMills("Using immutable collection: %d ms") {
    //val freq = str.map(c => (c, 1)).groupBy(_._1).map
  }

}
