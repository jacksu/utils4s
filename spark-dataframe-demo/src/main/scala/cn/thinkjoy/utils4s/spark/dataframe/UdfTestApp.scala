package cn.thinkjoy.utils4s.spark.dataframe

import java.text.SimpleDateFormat

import cn.thinkjoy.utils4s.spark.dataframe.udf.AccessLogParser

/**
 * Created by xbsu on 16/2/5.
 */
object UdfTestApp {
  def main(args: Array[String]) {
    val logAnalytics = new LogAnalytics
    println(logAnalytics.ip2City("120.132.74.17"))

    val rawRecord = """89.166.165.223 - - [25/Oct/2015:10:49:00 +0800] "GET /foo HTTP/1.1" 404 970 "-" "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.11) Firefox/3.0.11""""

    val parser = AccessLogParser
    val accessLogRecord = parser.parse(rawRecord) // an AccessLogRecord instance
    val logRecord = accessLogRecord.getOrElse(parser.nullObjectAccessLogRecord)
    println(s"******$logRecord******")
    val dateTime = logRecord.dateTime
    println(s"******$dateTime*****")
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    println(dateFormat.format(parser.parseDateField(dateTime).get))

    val agent = logRecord.userAgent
    println(s"agent:$agent")
  }
}
