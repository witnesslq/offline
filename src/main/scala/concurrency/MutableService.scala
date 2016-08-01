package concurrency

import collection.mutable.{HashMap => MutableHashMap}

/**
 * Created by fengwu on 15/5/23.
 */
class MutableService[Key, Value] extends Service[Key, Value] {
  val currentIndex = new MutableHashMap[Key, Value]

  override def lookup(key: Key): Option[Value] = synchronized(currentIndex.get(key))

  override def insert(key: Key, value: Value): Unit = synchronized {
    currentIndex.put(key, value);
  }
}
