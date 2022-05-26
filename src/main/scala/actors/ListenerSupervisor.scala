package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import messages.Message

class ListenerSupervisor(senderManager: ActorRef) extends Actor{

  override def receive: Receive = {

    case message: Message => {
      println("Message ID: " + message.id)

      // logs

      senderManager ! message
    }
  }
}
