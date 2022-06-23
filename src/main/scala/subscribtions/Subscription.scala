package subscribtions

import akka.actor.ActorRef

import scala.collection.mutable.ArrayBuffer

trait Subscription {
  def id: Int
  def from: ActorRef
  def topic: ArrayBuffer[String]
  def method: Int
}
