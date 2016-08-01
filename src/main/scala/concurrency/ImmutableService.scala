package concurrency

import java.io.File

import collection.immutable.{HashMap => ImmutableHashMap}

/**
 * Created by fengwu on 15/5/23.
 */
class ImmutableService[Key, Value] extends Service[Key, Value] {
  var currentIndex = new ImmutableHashMap[Key, Value]

  override def lookup(key: Key): Option[Value] = currentIndex.get(key)

  override def insert(key: Key, value: Value): Unit = synchronized {
    currentIndex = currentIndex + ((key, value)) // override source value
  }

  // Creating an object or returning a default
  def getTemporaryDirectory(tmpArg: Option[String]): java.io.File = {
    tmpArg.map(name => new File(name))
      .filter(_ isDirectory).getOrElse(new File(System.getProperty("java.io.tmpdir")))
  }



}
