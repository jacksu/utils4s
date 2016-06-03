package cn.thinkjoy.utils4s.nscala_time

import com.github.nscala_time.time._
import com.github.nscala_time.time.Imports._
import org.joda.time.PeriodType

/**
 * Hello world!
 *
 */
object BasicOper {
  def main(args: Array[String]) {
    //================= create date ===================
    println(DateTime.now())
    val yesterday = (DateTime.now() - 1.days).toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    println(yesterday)
    println(DateTime.parse("2014-07-7"))
    println(DateTime.parse("20140707", DateTimeFormat.forPattern("yyyyMMdd")))
    println(DateTime.parse("20140707", DateTimeFormat.forPattern("yyyyMMdd")).toLocalDate)
    println(DateTime.parse("20140707", DateTimeFormat.forPattern("yyyyMMdd")).toLocalTime)

    //============== compare two date ===========
    println(DateTime.parse("2014-07-7") < DateTime.parse("2014-07-8"))
    //println((DateTime.parse("2014-07-9").toLocalDate - DateTime.parse("2014-07-8").toLocalDate))


    // Find the time difference between two dates
    val newYear2016 = new DateTime withDate(2016, 1, 1)
    val daysToYear2016 = (newYear2016 to DateTime.now toPeriod PeriodType.days).getDays // 到2016年一月ㄧ日還有幾天

    // ========== manipulate dates =============
    println(DateTime.parse("2014-07-7") + 1.days)
    println((DateTime.parse("2014-07-7") + 1.day).toLocalDate)
    println(DateTime.parse("2014-07-7") - 1.days)
    println(DateTime.parse("2014-07-7") + (2 weeks))
    println(DateTime.parse("2014-07-7") + (2 months))
    println(DateTime.parse("2014-07-7") + (2 years))

    // ========== manipulate times =============
    println(DateTime.now() + 1.hour)
    println(DateTime.now() + 1.hour + 1.minute + 2.seconds)
    println(DateTime.now().getHourOfDay)
    println(DateTime.now.getMinuteOfHour)

    // ========== week related ops =============
    println((DateTime.now()-1.days).getDayOfWeek)//星期一为第一天
    println(DateTime.now().withDayOfWeek(1).toLocalDate)//这周的星期一
    println((DateTime.now()+ 1.weeks).withDayOfWeek(1))//下周星期一

    // ========== month related ops =============
    println((DateTime.now()-1.days).getDayOfMonth)
    println(DateTime.now().getMonthOfYear)
    println(DateTime.now().plusMonths(1))
    println(DateTime.now().dayOfMonth().getMaximumValue()) // 這個月有多少天

    // ========== year related ops =============
    println((DateTime.now()-1.days).getDayOfYear)
    println(DateTime.now().dayOfYear().getMaximumValue()) // 今年有多少天

  }
}
