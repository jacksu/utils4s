package cn.thinkjoy.utils4s.scala

import java.util.Date

/**
 * Created by jack on 15-12-22.
 */

trait Logger {
  def log(msg: String) {}
}

trait ConsoleLogger extends Logger {
  override def log(msg: String): Unit = {
    println(msg)
  }
}

trait TimeLogger extends Logger {
  override def log(msg: String) = {
    super.log(new Date() + "" + msg)
  }
}

trait ShortLogger extends Logger{
  //抽象字段
  val maxLength:Int
  override def log(msg:String): Unit ={
    if (msg.length<maxLength){
      super.log(msg)
    }else{
      super.log(msg.substring(0,maxLength)+"...")
    }
  }
}

class Account(balance: Double) extends Logger {
  def withdraw(amount: Double): Unit = {
    if (amount > balance) log("Insufficient funds")
  }
}

object TraitApp {
  def main(args: Array[String]) {
    //对象混入trait
    val account = new Account(1) with ConsoleLogger
    account.withdraw(2)

    //super.log调用的是下一个trait，具体是哪一个，要根据trait添加的顺序来决定
    val acc1= new Account(1) with ConsoleLogger with TimeLogger with ShortLogger{
      val maxLength=12
    }
    acc1.withdraw(2)
    val acc2=new Account(1) with ConsoleLogger with ShortLogger with TimeLogger{
      val maxLength=3
    }
    acc2.withdraw(2)
  }
}
