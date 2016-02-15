package cn.thinkjoy.utils4s.spark.dataframe

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{ HttpClients }
import org.json4s.JsonAST.JString
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by xbsu on 16/2/4.
 */

class LogAnalytics {

  /**
   * 通过IP返回IP所属城市
   * @param ip
   * @return
   */
  def ip2City(ip: String): String = {
    val location = ip2Location(ip)
    if (location.nonEmpty) {
      compact(render(parse(location) \ "city"))
    } else {
      ""
    }
  }

  /**
   * 通过IP返回IP所属城市
   * @param ip
   * @return
   */
  def ip2Province(ip: String): String = {
    val location = ip2Location(ip)
    if (location.nonEmpty) {
      compact(render(parse(location) \ "province"))
    } else {
      ""
    }
  }

  private def getRestContent(url: String): String = {
    val httpClient = HttpClients.createDefault()
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity()
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent()
      content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    httpClient.getConnectionManager().shutdown()
    return content
  }

  /**
   * 暂时没有超时，只是简单实现
   * @param ip
   * @return
   */
  private def ip2Location(ip: String): String = {
    val url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + ip
    val result = scala.io.Source.fromURL(url).mkString.split("=")(1)
    if ((parse(result) \ "ret").equals(JInt(1))) {
      org.apache.commons.lang.StringEscapeUtils.unescapeJava(result)
    } else {
      println(result)
      ""
    }
  }
}
