package actors

import actors.SenderSupervisor.{createSender, deleteSender}
import akka.actor.{Actor, ActorRef}
import subscribtions.{Subscription, SubscriptionDB}

class SenderScaler(senderSupervisor: ActorRef, senderManager: ActorRef) extends Actor{

  override def receive: Receive = {

    case subscription: Subscription => {
      var count: Int = 0

      if (subscription.method.equals(1)){ //Subscribe
//        println("This is sender scaler subscription method 1!")
        for (a <- SubscriptionDB.subscriptions.indices){
          if (SubscriptionDB.subscriptions(a).id.equals(subscription.id)){
            for (b <- subscription.topic.indices){
              SubscriptionDB.subscriptions(a).topic += subscription.topic(b)
//              println("Subscription ID: " + SubscriptionDB.subscriptions(a).id + " topic: " + subscription.topic(b))
            }

            count = 1
          }
        }

        if (count.equals(0)){
          //create new sender
//          println("Sender (sender scaler part): " + subscription.from.path)
          senderSupervisor ! createSender(subscription.id, subscription.from)
          SubscriptionDB.createNewSubscription(subscription)
//          println("Current number of subscribers: " + SubscriptionDB.subscriptions.length)
          for (a <- SubscriptionDB.subscriptions.indices){
            for (b <- SubscriptionDB.subscriptions(a).topic.indices){
//              println("Subscription ID: " + SubscriptionDB.subscriptions(a).id + " topic: " + SubscriptionDB.subscriptions(a).topic(b))
            }
          }

//          senderManager ! subscription
        }

//        senderManager.receive(subscriber)

      } else if (subscription.method.equals(2)){ //Unsubscribe
//        println("This is sender scaler subscription method 2!")
        for (a <- SubscriptionDB.subscriptions.indices){
          if (SubscriptionDB.subscriptions(a).id.equals(subscription.id)){
            SubscriptionDB.subscriptions(a).topic --= subscription.topic
//            println("Current number of subscribers: " + SubscriptionDB.subscriptions.length)
            for (a <- SubscriptionDB.subscriptions.indices){
              for (b <- SubscriptionDB.subscriptions(a).topic.indices){
//                println("Subscription ID: " + SubscriptionDB.subscriptions(a).id + " topic: " + SubscriptionDB.subscriptions(a).topic(b))
              }
            }
            count = SubscriptionDB.subscriptions(a).topic.length
          }
        }

        if (count.equals(0) ){
          //delete sender
          senderSupervisor ! deleteSender(subscription.id)
          SubscriptionDB.deleteSubscription(subscription)
//          println("Current number of subscribers: " + SubscriptionDB.subscriptions.length)
        }
      }
    }
  }
}
