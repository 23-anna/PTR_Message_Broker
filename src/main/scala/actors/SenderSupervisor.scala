package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import actors.SenderSupervisor.{createSender, deleteSender, sendMessage}
import messages.Message

import scala.collection.mutable.ArrayBuffer

object SenderSupervisor {
  case class createSender(subscriptionId: Int)
  case class deleteSender(subscriptionId: Int)
  case class sendMessage(message: Message, subscriptionId: Int)
}

class SenderSupervisor extends Actor{

  val system: ActorSystem = ActorSystem("Senders")
  var senderBuffer: ArrayBuffer[ActorRef] = ArrayBuffer[ActorRef]()

  override def receive: Receive = {

    case createSender(subscriptionId) => {
      println("Creation Works! " + subscriptionId)
      senderBuffer += system.actorOf(Props(new Sender(subscriptionId)), "ActorSender" + subscriptionId)
      println("Number of senders in buffer: " + senderBuffer.length)
    }

    case deleteSender(subscriptionId) => {
      println("Deletion Works! " + subscriptionId)
      for (a <- senderBuffer.indices){
        if (senderBuffer(a).path.name.equals("ActorSender" + subscriptionId)){
          senderBuffer.remove(a)
        }
      }
      println("Number of senders in buffer: " + senderBuffer.length)
    }

    case sendMessage(message, subscriptionId) => {
      println("Sending Works! " + subscriptionId)

      for (a <- senderBuffer.indices){
        if (senderBuffer(a).path.name.equals("ActorSender" + subscriptionId)){
          val actorSender = senderBuffer(a)
          actorSender ! message
        }
      }
    }
  }



}
