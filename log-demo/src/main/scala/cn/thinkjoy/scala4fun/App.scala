package cn.thinkjoy.scala4fun
import org.log4s._

/**
 * Hello world!
 *
 */

object App {
  case class ClassWithPrivateFields(name: String)

  def main(args: Array[String]) {
    val test=new LoggingTest
    test.logPrint()

    val log=getLogger
    log.debug("debug log")
    log.info("info log")
    log.warn("warn log")
    log.error("error log")

    val aClass = new ClassWithPrivateFields("sname")
    println(aClass.name)
  }

}
