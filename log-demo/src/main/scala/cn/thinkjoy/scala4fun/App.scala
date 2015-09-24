package cn.thinkjoy.scala4fun
import org.log4s._

/**
 * Hello world!
 *
 */
object App {
  def main(args: Array[String]) {
    val test=new LoggingTest
    test.logPrint()

    val log=getLogger
    log.debug("debug log")
    log.info("info log")
    log.warn("warn log")
    log.error("error log")
  }

}
