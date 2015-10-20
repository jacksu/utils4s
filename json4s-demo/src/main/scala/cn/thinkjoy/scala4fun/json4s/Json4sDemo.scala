package cn.thinkjoy.scala4fun.json4s

import org.json4s._
import org.json4s.jackson.JsonMethods._


object Json4sDemo{
  def main(args: Array[String]) {
    println(parse(""" { "numbers" : [1, 2, 3, 4] } """))
  }
}
