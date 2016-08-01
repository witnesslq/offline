package algorithm

/**
 * Created by fengwu on 15/2/11.
 */
object Prime extends App {

  def isPrime(n: Int): Boolean = {
    (2 until n) forall (n % _ != 0)
  }

  // Given a positive integer n , find all pairs of positive integers
  // i and j , with 1 <= i < j < n such that i + j is prime

  // flatten 将所有的序列连接 flatMap
  val a = ((1 until 7) flatMap (i => (1 until i) map (j => (i, j)))) filter (pair => isPrime(pair._1 + pair._2))

  def primePairs(n: Int) = {
    for {i <- 1 until n
         j <- 1 until i
         if (isPrime(i + j))
    } yield (i, j)
  }

  println(a)
  println(primePairs(7))
}
