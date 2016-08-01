package exercises

/**
 * Created by fengwu on 15/1/29.
 */
object Chapter14 extends App {

  // exercise 2
  def swap(p: Tuple2[Int, Int]) = p match {
    case (y, x) => (x, y)
  }

  val x = (1, 2)
  val y = swap(x)

  println(y)

  // exercise 3
  def swapFirstAndSecond(a: Array[Int]) = a match {
    case Array(x, y, rest@_*) => Array(y, x) ++ rest
    case _ => a
  }

  val arr = Array(1, 2, 3, 4)
  val brr = Array(1)

  //  println(swapFirstAndSecond(arr).mkString(" "))
  //  println(swapFirstAndSecond((brr)).mkString(" "))

  // exercise 4
  abstract class Item

  case class Article(description: String, price: Double) extends Item

  case class Bundle(description: String, discount: Double, items: Item*) extends Item

  case class Multiple(productNum: Int, item: Item) extends Item

  def price(item: Item): Double = item match {
    case Article(_, p) => p
    case Bundle(_, disc, iterms@_*) => iterms.map(price(_)).sum - disc
    case Multiple(num, item) => num * price(item)
  }

  val xp = Bundle("Father's day special", 20.0, Multiple(10, Article("scala for the Impatient", 39.95)),
    Bundle("Anchor Distillery Sampler", 10.0, Article("old potrero Straight Rye Whiskey", 79.95),
      Article("Junipero Gin", 32.95))
  )

  println(price(xp))

  // exercise 5 distinguish numbers and list by pattern match
  def leafSum(list: List[Any]): Int = (for (elem <- list) yield elem match {
    case x: List[Any] => leafSum(x)
    case x: Int => x
    case _ => 0
  }).sum

  // exercise 6 use binarytree do exercise 5
  sealed abstract class BinaryTree

  case class Leaf(value: Int) extends BinaryTree

  //case class Node(left: BinaryTree, right: BinaryTree) extends BinaryTree

  case class Node(op: Char, leafs: BinaryTree*) extends BinaryTree

  //  def leafSum(tree: BinaryTree): Int = tree match {
  //    case Leaf(x) => x
  //    case Node(Char ,leafs@ _*) => leafs.map(leafSum _).sum
  //  }

  def eval(tree: BinaryTree): Int = tree match {
    case Node(op, leafs@_*) => op match {
      case '+' => leafs.map(eval(_)).sum
      case '-' => -leafs.map(eval _).sum
      case '*' => leafs.map(eval(_)).product
    }
    case Leaf(x) => x
  }

  val listX = List(List(3, 8), 2, List(5))
  println(leafSum(listX))

  //  val treeX = Node(Node(Leaf(3), Leaf(8)), Leaf(2),  Node(Leaf(5)))
  val expressionX = Node('+', Node('*', Leaf(3), Leaf(8), Leaf(2), Node('-', Leaf(5))))
  println(eval(expressionX))

  // exercise 9
  def sum(lst: List[Option[Int]]) = lst.map(_.getOrElse(0)).sum

  val lst = List(Some(1), None, Some(2), None, Some(3))

  println(sum(lst))

  // exercise 10
  type T = Double => Option[Double]

  def compose(f: T, g: T): T = {
    (x: Double) => f(x) match {
      case Some(x) => g(x)
      case None => None
    }
  }

  def f(x: Double) = if (x >= 0) Some(Math.sqrt(x)) else None

  def g(x: Double) = if (x != 1) Some(1 / (x - 1)) else None

  val h = compose(f, g)

  println(h(4))
}
