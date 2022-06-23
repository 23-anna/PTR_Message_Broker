package actors

import java.time.LocalTime

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import actors.SenderSupervisor.{askAcknowledgement, createSender, deleteAcknowledgement, deleteSender, sendMessage, setAcknowledgement}
import log.Log
import messages.{Acknowledgement, Message}

import scala.collection.mutable.ArrayBuffer

object SenderSupervisor {
  case class createSender(subscriptionId: Int, sender: ActorRef)
  case class deleteSender(subscriptionId: Int)
  case class sendMessage(message: Message, subscriptionId: Int, sender: ActorRef)
  case class setAcknowledgement(sender: String)
  case class askAcknowledgement(sender: ActorRef)
  case class deleteAcknowledgement(id: Int)
}

//case object AskMessage

class SenderSupervisor extends Actor{

  val system: ActorSystem = ActorSystem("Senders")
  var senderBuffer: ArrayBuffer[ActorRef] = ArrayBuffer[ActorRef]()
  var acknowledgementBuffer: ArrayBuffer[Acknowledgement] = ArrayBuffer[Acknowledgement]()
  var ackId: Int = 0
  var senderCounter = 0

  override def receive: Receive = {

    case createSender(subscriptionId, sender: ActorRef) => {

//      println("Creation Works! " + subscriptionId)
      senderBuffer += system.actorOf(Props(new Sender(subscriptionId, sender)), "ActorSender" + subscriptionId)
      println(LocalTime.now() + " Sender ActorSender" + subscriptionId + " was created!")
      senderCounter += 1

//      println("Number of senders in buffer: " + senderBuffer.length)
    }

    case deleteSender(subscriptionId) => {
//      println("Deletion Works! " + subscriptionId)
      for (a <- senderBuffer.indices){
        if (senderBuffer(a).path.name.equals("ActorSender" + subscriptionId)){
          senderBuffer.remove(a)
        }
      }
//      println("Number of senders in buffer: " + senderBuffer.length)
    }

    case sendMessage(message, subscriptionId, sender) => {
//      println("Sending Works! " + subscriptionId)

      for (a <- senderBuffer.indices){
        if (senderBuffer(a).path.name.equals("ActorSender" + subscriptionId)){
          val actorSender = senderBuffer(a)
          actorSender ! message
//          Log.writeLog("Message " + message.id + " was sent to actor Sender " + actorSender.path.name)
        }
      }
    }

    case setAcknowledgement(sender) => {
      for (a <- senderBuffer.indices){
        if (senderBuffer(a).path.name.equals(sender)){
//          println("Actor found: " + senderBuffer(a).path.name)
//          val actorSender = senderBuffer(a)
          var newAcknowledgement: Acknowledgement = new Acknowledgement {
            override def id: Int = ackId

            override def actorName: String = senderBuffer(a).path.name

            override var status = 1
          }
          acknowledgementBuffer += newAcknowledgement

          ackId += 1

//          println("Were added ack params: " + acknowledgementBuffer(acknowledgementBuffer.length - 1).actorName + ", " +  acknowledgementBuffer(acknowledgementBuffer.length - 1).status)
//          actorSender ! "1"
        }
      }
    }

    case askAcknowledgement(actorSender) => {
//      println("This is askACK function!")
//      var status = new Acknowledgement
      val senderName = sender
      for (a <- acknowledgementBuffer.indices){
        if (acknowledgementBuffer(a).actorName.equals(actorSender.path.name)){
//          println("Actor found in ack buffer: " + acknowledgementBuffer(a).actorName)
//          status = acknowledgementBuffer(a)
          senderName ! acknowledgementBuffer(a)
//          println("Status was sent " + acknowledgementBuffer(a).status)
          acknowledgementBuffer(a).status = 0
          //          val actorSender = senderBuffer(a)
          //          actorSender ! "1"
//          sender ! status
//          println("Status was sent " + status)
        }
      }

    }

    case deleteAcknowledgement(id) => {
//      println("This is askACK delete function!")
//      var status: Int = 0
//      val senderName = sender
      for (a <- acknowledgementBuffer.indices){
        if (acknowledgementBuffer(a).id.equals(id)){
          acknowledgementBuffer.remove(a)
//          println("Actor found in ack buffer: " + acknowledgementBuffer(a).actorName)
//          status = acknowledgementBuffer(a).status
//          senderName ! status
//          println("Status was sent " + status)
//          acknowledgementBuffer(a).status = 0
          //          val actorSender = senderBuffer(a)
          //          actorSender ! "1"
          //          sender ! status
          //          println("Status was sent " + status)
        }
      }

    }


    case _ => println("that was unexpected")

//    case AskMessage => // respond to the 'ask' request
//      sender ! "Fred"
  }



}
