package buffer

import messages.Message

import scala.collection.mutable.ArrayBuffer

trait Queue {
//  def id: Int
  def name: String
  def messages: ArrayBuffer[Message]
}
