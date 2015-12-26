package cn.thinkjoy.utils4s.scala

/**
 * 只是为我平时做一些测试使用
 * Created by xbsu on 15/12/25.
 */
object TestApp {
  def getClickPoint(clickpoint: String) = {
    clickpoint.stripPrefix("(").stripSuffix(")").split(",")
  }

  def main(args: Array[String]) {
    getClickPoint("(2323,23)").foreach(println)
  }
}
