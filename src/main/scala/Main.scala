import java.util.concurrent.Flow.Subscriber

import actors.{Listener, ListenerSupervisor, Sender, SenderManager, SenderScaler, SenderSupervisor}
import akka.actor.{ActorSystem, Props}
import messages.Message
import subscribtions.Subscription

import scala.collection.mutable.ArrayBuffer

object Main extends App {
  println("Hello, it's me!")
  val message1 = new Message {
    override def id: Int = 10

    override def topic: ArrayBuffer[String] = ArrayBuffer("topic1", "topic2")

    override def body: String = "this is body1"
  }

  val message2 = new Message {
    override def id: Int = 20

    override def topic: ArrayBuffer[String] = ArrayBuffer("topic1", "topic3")

    override def body: String = "this is body2"
  }

  val subscription1 = new Subscription {
    override def id: Int = 30

    override def topic: ArrayBuffer[String] = ArrayBuffer("topic2", "topic3")

    override def method: Int = 1
  }

  val subscription2 = new Subscription {
    override def id: Int = 30

    override def topic: ArrayBuffer[String] = ArrayBuffer("topic1")

    override def method: Int = 1
  }

  val subscription3 = new Subscription {
    override def id: Int = 30

    override def topic: ArrayBuffer[String] = ArrayBuffer("topic2", "topic3")

    override def method: Int = 2
  }

  val system = ActorSystem("MessageBroker")

  val actorSenderSupervisor = system.actorOf(Props[SenderSupervisor], "ActorSenderSupervisor")
  val actorSenderManager = system.actorOf(Props(new SenderManager(actorSenderSupervisor)), "ActorSenderManager")
  val actorSenderScaler = system.actorOf(Props(new SenderScaler(actorSenderSupervisor)), "ActorSenderScaler")
  val actorListenerSupervisor = system.actorOf(Props(new ListenerSupervisor(actorSenderManager)), "ActorListenerSupervisor")
  val actorListener = system.actorOf(Props(new Listener(actorListenerSupervisor)), "ActorListener")

  actorListener ! message1
  actorListener ! message2

  actorSenderScaler ! subscription1
  actorSenderScaler ! subscription2
  actorSenderScaler ! subscription3


}