package cn.thinkjoy.utils4s.resources

import java.util.Properties

import scala.io.Source
import scala.xml.XML

/**
 * Hello world!
 *
 */
object ResourcesApp {
  def main(args: Array[String]): Unit = {
    val stream = getClass.getResourceAsStream("/test.properties")
    val prop=new Properties()
    prop.load(stream)
    println(prop.getProperty("url.jack"))
    //获取resources下面的文件
    val streamXml = getClass.getResourceAsStream("/test.xml")
    //val lines = Source.fromInputStream(streamXml).getLines.toList
    val xml=XML.load(streamXml)
    for (child <- xml \\ "collection" \\ "property"){
      println((child \\ "name").text)
      println((child \\ "url").text)
    }

  }
}
