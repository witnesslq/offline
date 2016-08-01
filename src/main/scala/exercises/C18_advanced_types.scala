package exercises

import scala.collection.mutable.ArrayBuffer

/**
 * Created by fengwu on 15/2/3.
 */
object C18_advanced_types extends App {

  class Network {
    class Member(val name: String) {
      val contacts = new ArrayBuffer[Network#Member]
    }

    private val members = new ArrayBuffer[Member]()

    def join(name: String) = {
      val m = new Member(name)
      members += m
      m
    }

    val chatter = new Network
    val myFaces = new Network
    val fred = chatter.join("Fred")
    val barney = myFaces.join("Barney")
    fred.contacts += barney
  }


}
