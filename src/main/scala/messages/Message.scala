package messages

import scala.collection.mutable.ArrayBuffer

trait Message {
  def id: Int
  def topic: ArrayBuffer[String]
  def body: String
}
