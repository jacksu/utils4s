package cn.thinkjoy.utils4s.log4s

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
