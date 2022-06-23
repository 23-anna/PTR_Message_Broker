package actors

import java.time.LocalTime

import actors.SenderSupervisor.{askAcknowledgement, deleteAcknowledgement, deleteSender}
import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.Write
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import messages.{Acknowledgement, Message}
import subscribtions.SubscriptionDB
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext, Future, TimeoutException}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import scala.util.control.Breaks.break

class Sender(Id: Int, sender: ActorRef) extends Actor{

  val id: Int = Id
  val consumer: ActorRef  = sender
  var timer: Long = 0
  var ack: Int = 0
  var counter_retry: Int = 0
  var future: Future[Any] = null
  var result: Acknowledgement = null
  var resultStatus: Int = 0

  override def receive: Receive = {
    case message: Message => {
      println(LocalTime.now() + " Message " + message.id + " was sent to the consumer " + id)
//      println("Message will be sent to: " + sender.path)
      var stringMessage: String = "Sender: " + self.path.name + " Topics: "
      for (a <- message.topic.indices){
        stringMessage = stringMessage + message.topic(a) + ", "
      }
      stringMessage = stringMessage + " Message body: "+ message.body
      sender ! Write(ByteString(stringMessage))
//      println("A message was sent and timer begins")

      Thread.sleep(5000)

      implicit val timeout: Timeout = Timeout(10 seconds)
      try {
        future = sender() ? askAcknowledgement(self)
        result = Await.result(future, timeout.duration).asInstanceOf[Acknowledgement]
        resultStatus = result.status
//        println(resultStatus + " = ACK ======================================================================================")

      } catch {
        case e: TimeoutException => counter_retry = 1
      }

          //      val future2: Future[Int] = ask(sender(), askAcknowledgement(self)).mapTo[Int]
          //      val result2 = Await.result(future2, 20 seconds)
          //      println(result2)

//          println(resultStatus + " = ACK ======================================================================================")
      if (counter_retry == 1) {
        while (counter_retry < 5){

          if (resultStatus != 1){
            try {
              future = sender() ? askAcknowledgement(self)
              result = Await.result(future, timeout.duration).asInstanceOf[Acknowledgement]
              resultStatus = result.status
            } catch {
              case e: TimeoutException => counter_retry += 1
            }
          } else {
            counter_retry = 20
          }
//          counter_retry += 1
//          println("Counter retry begins: " + counter_retry)


        }

        if (counter_retry equals 5){
          println(LocalTime.now() + " Stop actor " + self.path.name)
          println(LocalTime.now() + " Connection " + Id + " is killed")
          deleteSender(Id)
          SubscriptionDB.deleteSubscriptionById(Id)
          context.stop(self)
        }
      } else {
        sender() ! deleteAcknowledgement(result.id)
        counter_retry = 0
      }




//          if (counter_retry equals 11){
//            context.stop(self)
//          }


//      }

//      timer = System.nanoTime()
//
//      while (ack != 1 || System.nanoTime() - timer < 30000000000L){
//        println("Connection to consumer... try " + (counter_retry + 1))
//        sender ! Write(ByteString(stringMessage))
//        Thread.sleep(10000)
//        counter_retry += 1
//      }
//
//      if (ack == 0){
//        println("Connection " + Id + " is killed")
//        deleteSender(Id)
//        SubscriptionDB.deleteSubscriptionById(Id)
//        context.stop(self)
//      }

//      while (System.nanoTime() - timer < 30000000000L){
//      }
//
//      if (ack != 1){
//
//        while (counter_retry < 10) {
//          val timer2 = System.nanoTime()
//          while (System.nanoTime() - timer2 < 5000000000L){
//          }
//
//          if (ack == 0) {
//            sender ! Write(ByteString(stringMessage))
//            println("Connection to consumer... try " + (counter_retry + 1))
//            counter_retry += 1
//          } else {
//            ack = 0
//            counter_retry = 20
//          }
//        }
//
//        if (counter_retry == 10) {
//          //        kill the connection
//          println("Connection " + Id + " is killed")
//          deleteSender(Id)
//          SubscriptionDB.deleteSubscriptionById(Id)
//          context.stop(self)
//        }
//      }
    }

//    case acknowledgement: String => {
////      println("Consumer's response: " + s"${acknowledgement.utf8String}")
//      ack = 1
//      println("ACK = " + ack)
//    }
    case _ => println("that was unexpected")
  }
}
