package concurrency

/**
 * Created by fengwu on 15/5/23.
 */
trait Service[Key, Value] {
  def lookup(key: Key): Option[Value]

  def insert(key: Key, value: Value) : Unit
}
