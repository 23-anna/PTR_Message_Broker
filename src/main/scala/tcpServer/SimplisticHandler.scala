package tcpServer

import java.time.LocalTime

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp
import akka.util.ByteString
import actors.ListenerSupervisor.createListener
import actors.SenderSupervisor.setAcknowledgement
import actors.{SenderScaler, SenderSupervisor}
import subscribtions.{ListenerDB, Subscription}
import messages.Message
import subscribtions.ListenerDB.listeners

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

class SimplisticHandler(listenerSupervisor: ActorRef, senderScaler: ActorRef, senderSupervisor: ActorRef) extends Actor {
  import Tcp._
  var counter1 = 0
  var counter2 = 0
  var counter3 = 0

  def receive = {
    case Received(data) =>
      if (s"${data.utf8String}".equals("new producer")){
//        println("Method 1!")
        listenerSupervisor ! createListener(counter1)
        sender() ! Write(ByteString(ListenerDB.getListenerName(counter1).toString))
        counter1 += 1

      } else if (s"${data.utf8String}".contains("Message to")){
//        println("I got a message!")
        val newString = s"${data.utf8String}"
        val stringArray = newString.split(";")
        val topics = stringArray(1).substring(9).split(",")
//        println("stringArray(1): " + stringArray(1))
        val arrayBuffer = new ArrayBuffer[String]()
        for (a <- topics.indices){
          arrayBuffer.append(topics(a))
//          println(topics(a) + "++++++++++++++++++++++++++++++++++++++++++++++")
        }

        val message = new messages.Message {
          override def id: Int = counter3

          override def topic: ArrayBuffer[String] = arrayBuffer

          override def body: String = stringArray(2).substring(7)
        }

//        println(message.id + " " + message.body + " " + message.topic.length)

        println(LocalTime.now() + " New message: Id = " + message.id + " Topics = " + message.topic.mkString + " Body = " + message.body)

        ListenerDB.sendMessageFromListener(stringArray(0).substring(12), message)

        counter3 += 1

      } else if (s"${data.utf8String}".contains("Subscription")){
//        println("I got a subscription!")
        val newString = s"${data.utf8String}"
        val stringArray = newString.split(";")
        val topics = stringArray(1).substring(9).split(",")
        val arrayBuffer = new ArrayBuffer[String]()
        for (a <- topics.indices){
          arrayBuffer.append(topics(a))
//          println(topics(a) + "++++++++++++++++++++++++++++++++++++++++++++++")
        }
        val connection = sender()
        val subscription = new Subscription {
          override def id: Int = counter2

          override def from: ActorRef = connection

          override def topic: ArrayBuffer[String] = arrayBuffer

          override def method: Int = stringArray(0).substring(21).toInt
        }

        for (a <- subscription.topic.indices){
//          println(subscription.topic(a) + "----------------------------------------")
        }

        println(LocalTime.now() + " New subscription: Id = " + subscription.id +  " Method = " + subscription.method +" From = " + subscription.from +  " Topics = " + subscription.topic.mkString)
//        println(subscription.id + " " + subscription.method + " " + subscription.topic.length + " sender: " + subscription.from.path)

        senderScaler ! subscription

        counter2 += 1
      } else if (s"${data.utf8String}".contains("Acknowledgement!")){
        println(LocalTime.now() + " " + s"${data.utf8String}")
        val newSubstring = s"${data.utf8String}".substring(40, 52)
//        println(newSubstring)
        senderSupervisor ! setAcknowledgement(newSubstring)
      }


//        println(s"Data received - ${data.utf8String}")
//        sender() ! Write(ByteString("SERVER_RES: ").concat(data))

//    case Received(message: messages.Message) => {
//      println("Data received - Message id: " + message.id + " Message body: " + message.body)
//    }

    case PeerClosed     => context stop self
  }
}
