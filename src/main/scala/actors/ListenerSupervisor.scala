package actors

import java.time.LocalTime

import actors.ListenerSupervisor.createListener
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import log.Log
import messages.Message

import scala.collection.mutable.ArrayBuffer

object ListenerSupervisor {
  case class createListener(id: Int)
//  case class deleteSender(subscriptionId: Int)
//  case class sendMessage(message: Message, subscriptionId: Int)
}

class ListenerSupervisor(senderManager: ActorRef) extends Actor{

  val system: ActorSystem = ActorSystem("Listeners")
  var listenerBuffer: ArrayBuffer[ActorRef] = ArrayBuffer[ActorRef]()

  override def receive: Receive = {

    case createListener(id: Int) => {

      //      println("Creation of listener Works! " + subscriptionId)
//      listenerBuffer += system.actorOf(Props(new Listener(senderManager, id)), "ActorListener" + id)
      subscribtions.ListenerDB.listeners += system.actorOf(Props(new Listener(senderManager, id)), "ActorListener" + id)
      println(LocalTime.now() + " Listener ActorListener" + id + " was created!")
//      println("Number of listeners in buffer: " + listenerBuffer.length)
    }




//
//    case message: Message => {
//      println("Message ID: " + message.id)
//
//      // logs
//      Log.writeLog("Message arrived: " + message.id)

//      senderManager ! message
//    }
  }
}
