package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import buffer.PersistentQueues
import messages.Message

class Listener(listenerSupervisor: ActorRef) extends Actor{

//  var listenerSupervisor: ListenerSupervisor = null

  override def receive: Receive = {

//    case actorListenerSupervisor: ListenerSupervisor => {
//      listenerSupervisor = actorListenerSupervisor
//    }

    case message: Message => {
      println(message.id + " " + message.body)

      for (a <- message.topic.indices){
        var check: Int = 0
        val len: Int = PersistentQueues.buffer.length
        if (PersistentQueues.buffer.nonEmpty){
          for (b <- 0 until len){
            if (message.topic(a).equals(PersistentQueues.buffer(b).name)){
              PersistentQueues.buffer(b).messages.append(message)
              println("Added a new message " + PersistentQueues.buffer(b).messages(PersistentQueues.buffer(b).messages.length-1).id + " to queue " + PersistentQueues.buffer(b).name)
              check = 1
            }
            if (check.equals(0) && b.equals(len - 1)){
              PersistentQueues.createQueue(message.topic(a), message)
              println("Added a new queue: " + PersistentQueues.buffer(b + 1).name)
              println("Added a new message " + PersistentQueues.buffer(b + 1).messages(0).id + " to queue " + PersistentQueues.buffer(b + 1).name)
            }
          }
          check = 0
        } else {
          PersistentQueues.createQueue(message.topic(a), message)
          println("Added a new queue: " + PersistentQueues.buffer(0).name)
          println("Added a new message " + PersistentQueues.buffer(0).messages(0).id + " to queue " + PersistentQueues.buffer(0).name)
        }
      }


//      val system = ActorSystem("Listener")
//
//      val actorListenerSupervisor = system.actorOf(Props[ListenerSupervisor], "ActorListenerSupervisor")
//      actorListenerSupervisor ! message.id
      listenerSupervisor ! message

    }
  }

}
