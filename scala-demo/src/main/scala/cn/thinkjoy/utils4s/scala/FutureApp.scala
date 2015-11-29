package cn.thinkjoy.utils4s.scala

import scala.concurrent.{TimeoutException, Await, Future}
import scala.util.{Try, Failure, Success, Random}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by jacksu on 15-11-27.
 */

/**
 * 准备一杯卡布奇诺
 * 1 研磨所需的咖啡豆
 * 2 加热一些水
 * 3 用研磨好的咖啡豆和热水制做一杯咖啡
 * 4 打奶泡
 * 5 结合咖啡和奶泡做成卡布奇诺
 */
object FutureApp {
  // Some type aliases, just for getting more meaningful method signatures:
  type CoffeeBeans = String
  type GroundCoffee = String

  case class Water(temperature: Int)

  type Milk = String
  type FrothedMilk = String
  type Espresso = String
  type Cappuccino = String

  // some exceptions for things that might go wrong in the individual steps
  // (we'll need some of them later, use the others when experimenting with the code):
  case class GrindingException(msg: String) extends Exception(msg)

  case class FrothingException(msg: String) extends Exception(msg)

  case class WaterBoilingException(msg: String) extends Exception(msg)

  case class BrewingException(msg: String) extends Exception(msg)

  def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
    println("start grinding...")
    Thread.sleep(Random.nextInt(200))
    if (beans == "baked beans") throw GrindingException("are you joking?")
    println("finished grinding...")
    s"ground coffee of $beans"
  }

  def heatWater(water: Water): Future[Water] = Future {
    println("heating the water now")
    Thread.sleep(Random.nextInt(200))
    println("hot, it's hot!")
    water.copy(temperature = 85)
  }

  def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
    println("milk frothing system engaged!")
    Thread.sleep(Random.nextInt(200))
    println("shutting down milk frothing system")
    s"frothed $milk"
  }

  def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = Future {
    println("happy brewing :)")
    Thread.sleep(Random.nextInt(200))
    println("it's brewed!")
    "espresso"
  }

  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"


  def prepareCappuccinoSequentially(): Future[Cappuccino] = {
    for {
      ground <- grind("arabica beans")
      water <- heatWater(Water(25))
      foam <- frothMilk("milk")
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)
  }

  def prepareCappuccino(): Future[Cappuccino] = {
    val groundCoffee = grind("arabica beans")
    val heatedWater = heatWater(Water(20))
    val frothedMilk = frothMilk("milk")
    for {
      ground <- groundCoffee
      water <- heatedWater
      foam <- frothedMilk
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)
  }


  def main(args: Array[String]) {

    //回调函数
    grind("baked beans").onComplete {
      case Success(ground) => println(s"got my $ground")
      case Failure(ex) => println("This grinder needs a replacement, seriously!")
    }
    //Await.result(f,1 milli)

    //顺序,并且为了测试try,主线程等待结果的完成
    val result=Try(Await.result(prepareCappuccinoSequentially(), 1 second)) recover {
      case e:TimeoutException => "timeout error"
    }
    println(result.get)
    //并行
    Await.result(prepareCappuccino(), 1 second)
    //cap.collect()
    //Thread.sleep(Random.nextInt(2000))

  }
}
