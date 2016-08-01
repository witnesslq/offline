def digits(n: Int): Set[Int] =
  if (n < 0) digits(-n)
  else if (n < 10) Set(n)
  else digits(n / 10) + (n % 10)

println(digits(1121))
9 :: List(4, 2)
def sum(lst: List[Int]): Int =
  if (lst == Nil) 0 else lst.head + sum(lst.tail)

println(sum(9 :: List(4, 2)))
List(4, 2) :+ 9
def sumPatternMatch(lst: List[Int]): Int = lst match {
  case Nil => 0
  case h :: t => h + sum(t)
}

println(sumPatternMatch(9 :: List(4, 2)))

List(4, 2).sum
val digitsImmutable = Set(1, 7, 2, 9)
val primes = Set(2, 3, 5, 9)
digitsImmutable union primes // ++
digitsImmutable intersect primes // &
digitsImmutable diff primes // --
Vector(1, 2, 3) :+ 5
1 +: Vector(1, 2, 3)
Vector(1, 2, 3, 4, 5).drop(2)
Vector(1, 2, 3, 4, 5).dropRight(2)
Vector(1, 2, 3, 4, 5).slice(2, 4)
Vector(1, 2, 3, 4, 5).grouped(5)

"Mississippi".foldLeft(Map[Char, Int]())((m, c) => m + (c -> (m.getOrElse(c, 0) + 1)))
(1 to 10).scanLeft(0)(_ + _)
// stream
def numsFrom(n: BigInt): Stream[BigInt] = n #:: numsFrom(n + 1)
val tenOrMore = numsFrom(10)
tenOrMore.tail.tail.tail

val squares = numsFrom(1).map(x => x * x)

squares.take(5).force
//  lazy view
val powers = (0 until 1000).view.map(scala.math.pow(10, _))

for (i <- (0 until 100).par) print(i + " ")

"Mississippi".par.aggregate(Set[Char]())(_ + _, _ ++ _)

"Mississippi".zipWithIndex.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
List(1, 2, 3).foldLeft(List[Int]())(_ :+ _)
List(1, 2, 3).foldRight(List[Int]())(_ :: _)
List(1, 2, 3).foldLeft(List[Int]())((x, y) => y :: x)

val y = (List[Int]() /: List(1, 2, 3))(_ :+ _)

val grups = Array(1, 2, 3, 4, 5, 6).grouped(3)
while (grups hasNext)
  println(grups next() toList)
((1 until 7) flatMap (i => (1 until i) map (j => (i, j))))

val m1 = Map(1 -> 2.0, 3 -> 4.0, 5 -> 6.2)
val m2 = Map(0 -> 3.0, 3 -> 7.0)
m2 ++ m1

m1 ++ m2




