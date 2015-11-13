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

    val loggerName = this.getClass.getName
    val log=getLogger(loggerName)
    log.debug("debug log")
    log.info("info log")
    log.warn("warn log")
    log.error("error log")

  }

}
