package actors

import java.io.FileWriter
import java.time.LocalTime

import akka.actor.{Actor, ActorRef}
import buffer.PersistentQueues
import messages.Message

class Listener(senderManager: ActorRef, id: Int) extends Actor{

  val listener_id = id
  override def receive: Receive = {

    case message: Message => {
//      println(message.id + " " + message.body)

      for (a <- message.topic.indices){
        var check: Int = 0
        val len: Int = PersistentQueues.buffer.length
        if (PersistentQueues.buffer.nonEmpty){
          for (b <- 0 until len){
            if (message.topic(a).equals(PersistentQueues.buffer(b).name)){
              PersistentQueues.buffer(b).messages.append(message)
              println(LocalTime.now() + " Added a new message " + PersistentQueues.buffer(b).messages(PersistentQueues.buffer(b).messages.length-1).id + " to queue " + PersistentQueues.buffer(b).name)
              check = 1
              val fileName = PersistentQueues.buffer(b).name + ".txt"
              val bufferedSource = scala.io.Source.fromFile(fileName).getLines().toList.mkString
              val newString = "{ " + "id: " + message.id + ", topic: " + message.topic + ", body: " + message.body + " };\n"
              val fileWriter = new FileWriter(fileName)
              fileWriter.write(bufferedSource + newString)
              fileWriter.close()
//              bufferedSource.close
            }
            if (check.equals(0) && b.equals(len - 1)){
              PersistentQueues.createQueue(message.topic(a), message)
              println(LocalTime.now() + " Added a new queue: " + PersistentQueues.buffer(b + 1).name)
              println(LocalTime.now() + " Added a new message " + PersistentQueues.buffer(b + 1).messages(0).id + " to queue " + PersistentQueues.buffer(b + 1).name)
            }
          }
          check = 0
        } else {
          PersistentQueues.createQueue(message.topic(a), message)
          println(LocalTime.now() + " Added a new queue: " + PersistentQueues.buffer(0).name)
          println(LocalTime.now() + " Added a new message " + PersistentQueues.buffer(0).messages(0).id + " to queue " + PersistentQueues.buffer(0).name)
        }
      }

      senderManager ! message

    }
  }

}
