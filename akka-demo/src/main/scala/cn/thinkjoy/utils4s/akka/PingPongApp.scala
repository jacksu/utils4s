package cn.thinkjoy.utils4s.akka

import akka.actor._
/**
 * Created by jacksu on 15/12/26.
 */

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

class Ping(pong: ActorRef) extends Actor {
  var count = 0
  def incrementAndPrint { count += 1; println("ping") }
  def receive = {
    case StartMessage =>
      incrementAndPrint
      pong ! PingMessage
    case PongMessage =>
      if (count > 9) {
        sender ! StopMessage
        println("ping stopped")
        context.stop(self)
      } else {
        incrementAndPrint
        sender ! PingMessage
      }
  }
}

class Pong extends Actor {
  def receive = {
    case PingMessage =>
      println("  pong")
      sender ! PongMessage
    case StopMessage =>
      println("pong stopped")
      context.stop(self)
      context.system.shutdown()
  }
}
object PingPongApp {
  def main(args: Array[String]) {
    val system = ActorSystem("PingPongSystem")
    val pong = system.actorOf(Props[Pong], name = "pong")
    val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
    // start them going
    ping ! StartMessage
  }
}
