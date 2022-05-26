package actors

import akka.actor.Actor
import messages.Message

class Sender(Id: Int) extends Actor{

  val id: Int = Id

  override def receive: Receive = {
    case message: Message => {
      println("Message " + message.id + " was sent to the consumer " + id)
    }

    case acknowledgement: Int => {
      println("Message was delivered" + acknowledgement)
    }
  }
}
