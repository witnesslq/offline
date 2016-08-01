package exercises

import scala.io.Source

/**
 * Created by fengwu on 15/2/26.
 */
object X extends App {
  val in = Source.fromFile("/Users/fengwu/Documents/test.txt")
  val ls = in.getLines.toList
  val words = ls.filter (word => word forall (chr => chr.isLetter))
  println(words)
  val mnem = Map('2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL",
    '6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WXYZ")

  val charCode: Map[Char, Char] =
    for ((digit, str) <- mnem; ltr <- str) yield ltr -> digit

  def wordCode(word: String): String = {
    word.toUpperCase map charCode
  }

  /**
   * A map from digit strings to the words that represent them,
   * eg: "5282" -> List("Java", "Kava", "Lava", ....)
   * Note: A missing number should map to the empty set, e.g "1111" -> List()
   */
  def wordsForNum: Map[String, Seq[String]] = {
    words groupBy wordCode
  }

  println(wordsForNum)

}
