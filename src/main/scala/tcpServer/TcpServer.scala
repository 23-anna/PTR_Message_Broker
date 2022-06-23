package tcpServer

import java.net.InetSocketAddress

import actors.{ListenerSupervisor, SenderScaler, SenderSupervisor}
import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}

//object TcpServer {
//  def props(remote: InetSocketAddress) =
//    Props(new TcpServer(remote), )
//}

class TcpServer(remote: InetSocketAddress, listenerSupervisor: ActorRef, senderScaler: ActorRef, senderSupervisor: ActorRef) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, remote)

  def receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) ⇒ context stop self

    case c @ Connected(remote, local) =>
      println(s"Client connected - Remote(Client): ${remote.getAddress} Local(Server): ${local.getAddress}")
      val handler = context.actorOf(Props(new SimplisticHandler(listenerSupervisor, senderScaler, senderSupervisor)))
      val connection = sender()
      connection ! Register(handler)
  }

}