package actors

import actors.SenderSupervisor.sendMessage
import akka.actor.{Actor, ActorRef}
import buffer.{PersistentQueues, Queue}
import messages.Message
import subscribtions.{Subscription, SubscriptionDB}

import scala.collection.convert.ImplicitConversions.`buffer AsJavaList`
import scala.collection.mutable.ArrayBuffer

class SenderManager(senderSupervisor: ActorRef) extends Actor{

  override def receive: Receive = {

    case message: Message => {
//      println("Message ID (sender manager part): " + message.id)

      for (a <- SubscriptionDB.subscriptions.indices){
        var filteredMessage = ArrayBuffer[Message]()
        for (b <- SubscriptionDB.subscriptions(a).topic.indices){
          for (c <- message.topic.indices) {
            if (SubscriptionDB.subscriptions(a).topic(b).equals(message.topic(c))){
              filteredMessage += message
            }
          }
        }

//        println("---------------------------------------" + SubscriptionDB.subscriptions(a).id + "++++++++++++++++++" + SubscriptionDB.subscriptions(a).from)
        if (filteredMessage.nonEmpty) {
          senderSupervisor ! sendMessage(filteredMessage.head, SubscriptionDB.subscriptions(a).id, SubscriptionDB.subscriptions(a).from)
        }

        filteredMessage.clear()
      }

    }

    case subscription: Subscription => {
      var messageArray = ArrayBuffer[Message]()

      for (a <- subscription.topic.indices){
//        println("Test: " + PersistentQueues.getQueue(subscription.topic(a)) + "++++++++++++++++++++++++++")
//        messageArray.addAll(PersistentQueues.getQueue(subscription.topic(a))
        for (b <- PersistentQueues.buffer.indices){
          if (PersistentQueues.buffer(b).name.equals(subscription.topic(a))){
            messageArray.addAll(PersistentQueues.buffer(b).messages)
//            println("Test: " + PersistentQueues.buffer(b).name + "++++++++++++++++++++++++++")
          }
        }
      }
//      println("--------------------------------------------------------- Message array: " + messageArray.length)
      for (a <- 0 to messageArray.length-2){
        val currentMessage = messageArray(a)
        if (currentMessage.id.equals(messageArray(a+1).id)){
          messageArray.remove(a)
        }
      }
//      println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Message array: " + messageArray.length)
    }

  }
}
