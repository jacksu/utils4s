package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15-12-4.
 */
object EnumerationApp {
  object TrafficLightColor extends Enumeration{
    type TrafficLightColor = Value
    val Red = Value(0,"stop")
    val Yellow = Value(10)
    val Green = Value("go")
  }

  import TrafficLightColor._

  def doWhat(color:TrafficLightColor): Unit =color match{
    case Red => println("stop")
  }
  def main(args: Array[String]) {
    doWhat(TrafficLightColor(0))
    println(Green.id+","+Green)
    println(TrafficLightColor(0))
    println(TrafficLightColor(10))
    println(TrafficLightColor.withName("stop").id)
  }
}
