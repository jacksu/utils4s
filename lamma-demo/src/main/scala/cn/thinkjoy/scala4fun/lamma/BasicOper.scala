package cn.thinkjoy.scala4fun.lamma

import io.lamma._

/**
 * test
 *
 */
object BasicOper {
  def main(args: Array[String]): Unit = {
    //============== compare two date ===========
    println(Date(2014, 7, 7) < Date(2014, 7, 8))
    println((2014, 7, 7) <(2014, 7, 8))
    println(Date("2014-07-7") > Date("2014-7-8"))

    // ========== manipulate dates =============
    println(Date(2014, 7, 7) + 1)
    println(Date("2014-07-7") + 1)
    println(Date("2014-07-7") - 1)
  }
}
