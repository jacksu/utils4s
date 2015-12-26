package cn.thinkjoy.utils4s.akka

import akka.actor.{Props, ActorSystem, Actor}

/**
 * Created by jacksu on 15/12/26.
 */

class HelloActor extends Actor{
  def receive = {
    case "hello" => println("您好！")
    case _       => println("您是?")
  }
}

object HelloWorldApp {
  def main(args: Array[String]) {
    val system = ActorSystem("HelloSystem")
    // 缺省的Actor构造函数
    val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
    helloActor ! "hello"
    helloActor ! "喂"
  }
}
