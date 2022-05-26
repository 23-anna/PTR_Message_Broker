package actors

import actors.SenderSupervisor.sendMessage
import akka.actor.{Actor, ActorRef}
import buffer.{PersistentQueues, Queue}
import messages.Message
import subscribtions.{Subscription, SubscriptionDB}

import scala.collection.mutable.ArrayBuffer

class SenderManager(senderSupervisor: ActorRef) extends Actor{

  override def receive: Receive = {

    case message: Message => {
      println("Message ID (sender manager part): " + message.id)

      for (a <- SubscriptionDB.subscriptions.indices){
        var filteredMessage = ArrayBuffer[Message]()
        for (b <- SubscriptionDB.subscriptions(a).topic.indices){
          for (c <- message.topic.indices) {
            if (SubscriptionDB.subscriptions(a).topic(b).equals(message.topic(c))){
              filteredMessage += message
            }
          }
        }

        senderSupervisor ! sendMessage(filteredMessage.head, SubscriptionDB.subscriptions(a).id)

        filteredMessage.clear()
      }

    }

  }
}
