package subscribtions

import scala.collection.mutable.ArrayBuffer

object SubscriptionDB {

  val subscriptions = new ArrayBuffer[Subscription]()

  def createNewSubscription(subscription: Subscription) = {
    subscriptions += subscription
  }

  def deleteSubscription(subscription: Subscription) = {
//    println("deleteSubscription()")
    for (a <- subscriptions.indices){
      if (subscriptions(a).id.equals(subscription.id)){
        subscriptions.remove(a)
      }
    }
    println("Current number of subscribers: " + subscriptions.length)
  }

  def deleteSubscriptionById(subscriptionId: Int) = {
//    println("deleteSubscriptionById()")
    for (a <- subscriptions.indices){
      if (subscriptions(a).id.equals(subscriptionId)){
        subscriptions.remove(a)
      }
    }
    println("Current number of subscribers: " + subscriptions.length)
  }

}
