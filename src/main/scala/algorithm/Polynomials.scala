package algorithm

/**
 * Created by fengwu on 15/2/12.
 */
object Polynomials extends App {

  class Poly(val terms: Map[Int, Double]) {
    def +(other: Poly) = new Poly(terms ++ (other.terms map adjust))
    def adjust(term :(Int, Double)) : (Int , Double) ={
      val (exp, coeff) = term
      terms get exp match {
        case Some(coffe1) => exp -> (coffe1 + coffe1)
        case None => exp -> coeff
      }
    }
    override def toString =
      (for ((exp, coeff) <- terms) yield coeff + "x^" + exp) mkString "+"
  }

  val p1 = new Poly(Map(1 -> 2.0, 3 -> 4.0, 5 -> 6.2))
  val p2 = new Poly(Map(0 -> 3.0, 3 -> 7.0))

  println(p1 + p2)
}
