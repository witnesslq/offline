package exercises

/**
 * Created by fengwu on 15/1/27.
 */
object Chapter5 extends App {

  case class Container[+A](value: A)

  val double = Container(3.3)
  var container: Container[Any] = double

  container match {
    case c: Container[String] => println(c.value.toUpperCase)
    case c: Container[Double] => println(math.sqrt(c.value))
    case _ => println("_")
  }

  class Time(val hrs: Int, val min: Int) {
    def before(other: Time) = hrs < other.hrs || hrs == other.hrs && min < other.min

    override def toString = hrs + ":" + min
  }

  // exercise 7
  class Person(name: String) {
    val Array(firstName, lastName) = name.split(' ')
  }

  val p = new Person("wu feng")
  println(p.lastName + ", " + p.firstName)
}
