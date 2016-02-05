package cn.thinkjoy.utils4s.spark.dataframe

import cn.thinkjoy.utils4s.spark.dataframe.udf.AccessLogParser

/**
  * Created by xbsu on 16/2/5.
  */
object UdfTestApp {
  def main(args: Array[String]) {
    val logAnalytics = new LogAnalytics
    println(logAnalytics.ip2City("120.132.74.17"))

    val rawRecord = """89.166.165.223 - - [21/Jul/2009:02:48:12 -0700] "GET /foo HTTP/1.1" 404 970 "-" "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.11) Firefox/3.0.11""""

    val parser = AccessLogParser
    val accessLogRecord = parser.parse(rawRecord)    // an AccessLogRecord instance
    println(accessLogRecord)
  }
}
