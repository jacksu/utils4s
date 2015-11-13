package cn.thinkjoy.scala4fun

import org.log4s._

/**
 * Created by jacksu on 15/9/24.
 */


class LoggingTest extends Logging{
  def logPrint(): Unit ={
    logger.debug("debug log")
    logger.info("info log")
    logger.warn("warn log")
    logger.error("error log")
  }
}
