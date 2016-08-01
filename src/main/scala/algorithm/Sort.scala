package algorithm

/**
 * Created by fengwu on 15/2/11.
 */
object Sort extends App {

  //def msort[T](xs: List[T])(lt:(T, T) => Boolean): List[T] = {
  def msort[T](xs: List[T])(implicit ord: Ordering[T]): List[T] = {
    val n = xs.length / 2
    if (n == 0) xs
    else {
      def merge(xs: List[T], ys: List[T]): List[T] = {
        //        xs match {
        //          case Nil => ys
        //          case x :: xs1 =>
        //            ys match {
        //              case Nil => xs
        //              case y :: ys1 =>
        //                if (x < y) x :: merge(xs1, ys)
        //                else y :: merge(xs, ys1)
        //            }
        // using a pattern match over pairs
        (xs, ys) match {
          case (Nil, ys) => ys
          case (xs, Nil) => xs
          case (x :: xs1, y :: ys1) =>
            if (ord.lt(x, y)) x :: merge(xs1, ys)
            else y :: merge(xs, ys1)
        }
      }
      val (fst, snd) = xs splitAt n
      merge(msort(fst), msort(snd))
    }
  }


  def pack[T](xs: List[T]): List[List[T]] = {
    xs match {
      case Nil => Nil
      case x :: xs1 => val (first, rest) = xs.span(y => y == x)
        first :: pack(rest)
    }
  }

  def encode[T](xs: List[T]): List[(T, Int)] = {
    pack(xs).map(ys => (ys.head, ys.length))
  }


  println(encode(List(3, 2, 10, 9, 3, 3, 8, 3, 4, 2)))
  //println(msort(List("wufeng", "hello")))


}
