package cn.thinkjoy.utils4s.lamma

import io.lamma._

/**
 * test
 *
 */
object BasicOper {
  def main(args: Array[String]): Unit = {
    //============== create date ===========
    println(Date(2014, 7, 7).toISOString) //2014-07-07
    println(Date("2014-07-7").toISOInt) //20140707
    println(Date.today())

    //============== compare two date ===========
    println(Date(2014, 7, 7) < Date(2014, 7, 8))
    println((2014, 7, 7) <(2014, 7, 8))
    println(Date("2014-07-7") > Date("2014-7-8"))
    println(Date("2014-07-10") - Date("2014-7-8"))

    // ========== manipulate dates =============
    println(Date(2014, 7, 7) + 1)
    println((2014, 7, 7) + 30)
    println(Date("2014-07-7") + 1)
    println(Date("2014-07-7") - 1)
    println(Date("2014-07-7") + (2 weeks))
    println(Date("2014-07-7") + (2 months))
    println(Date("2014-07-7") + (2 years))

    // ========== week related ops ============
    println(Date("2014-07-7").dayOfWeek) //MONDAY
    println(Date("2014-07-7").withDayOfWeek(Monday).toISOString) //这周的星期一 2014-07-07
    println(Date("2014-07-7").next(Monday))
    println(Date(2014, 7, 8).daysOfWeek(0)) //默认星期一是一周第一天

    // ========== month related ops ============
    println(Date("2014-07-7").maxDayOfMonth)
    println(Date("2014-07-7").lastDayOfMonth)
    println(Date("2014-07-7").firstDayOfMonth)
    println(Date("2014-07-7").sameWeekdaysOfMonth)
    println(Date("2014-07-7").dayOfMonth)

    // ========== year related ops ============
    println(Date("2014-07-7").maxDayOfYear)
    println(Date("2014-07-7").dayOfYear)
  }
}
