package subscribtions

import scala.collection.mutable.ArrayBuffer

trait Subscription {
  def id: Int
  def topic: ArrayBuffer[String]
  def method: Int
}
