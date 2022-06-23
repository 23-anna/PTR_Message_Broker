package subscribtions

import akka.actor.{ActorRef, actorRef2Scala}
import messages.Message

import scala.collection.mutable.ArrayBuffer

object ListenerDB {
  val listeners = new ArrayBuffer[ActorRef]()

  def getListenerByName(name: String): Any = {
    for (a <- listeners.indices){
      if (listeners(a).path.name.equals(name)){
        return listeners(a)
      }
    }

  }

  def sendMessageFromListener(name: String, message: Message) = {
    for (a <- listeners.indices){
      if (listeners(a).path.name.equals(name)){
        listeners(a) ! message
      }
    }

  }

  def getListenerName(id: Int): Any = {
    for (a <- listeners.indices){
      if (listeners(a).path.name.equals("ActorListener" + id)){
        return listeners(a).path.name
      }
    }

  }
}
