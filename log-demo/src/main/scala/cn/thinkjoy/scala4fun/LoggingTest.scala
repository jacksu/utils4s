package cn.thinkjoy.scala4fun

import org.log4s._

/**
 * Created by xbsu on 15/9/24.
 */

case class ClassWithPrivateFields(name: String){

}

class LoggingTest {
  private[this] val log=getLogger
  def logPrint(): Unit ={
    log.debug("debug log")
    log.info("info log")
    log.warn("warn log")
    log.error("error log")
    val test=ClassWithPrivateFields("test")
    test.name
  }
}
