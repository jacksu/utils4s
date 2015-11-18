package cn.thinkjoy.scala4fun.twitter.util.core

import com.twitter.conversions.time._

object TimeApp {
  def main(args: Array[String]) {
    val duration1 = 1.second
    val duration2 = 2.minutes
    //duration1.inMillis
    println( duration1.inMilliseconds )
    println((duration2-duration1).inSeconds)
    println((duration2-duration1).inMinutes)

  }

}
