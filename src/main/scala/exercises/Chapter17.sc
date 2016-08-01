class Pair[T <% Ordered[T]](val first: T, val second: T){
  def smaller = if (first < second) first else second
}

class Pair2[T:Ordering](val first: T, val second : T){

}

class Pair3[+T](var first:T, var second :T)