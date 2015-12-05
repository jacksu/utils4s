package cn.thinkjoy.utils4s.scala

import java.net.URL

import scala.io.Source
import scala.sys.process.ProcessBuilder.URLBuilder

/**
 * Created by jack on 15-12-5.
 */

object FileSysCommandApp {
  def main(args: Array[String]) {
    val source = Source.fromURL("http://www.baidu.com","UTF-8")
    println(source.mkString)
    import sys.process._
    "ls -la ." !
    val result = "ls -l ." #| "grep README" #| "wc -l" !!
    //!!必须空一行

    println(result)
    "grep baidu" #< new URL("http://www.baidu.com") !
  }

}
