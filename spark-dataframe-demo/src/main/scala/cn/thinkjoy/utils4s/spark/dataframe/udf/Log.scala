package cn.thinkjoy.utils4s.spark.dataframe

/**
  * Created by xbsu on 16/2/4.
  */
package object Log {
  def ip2Location(ip:String): String ={
    val url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + ip
    scala.io.Source.fromURL(url).mkString
  }
}
