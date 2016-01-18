package cn.thinkjoy.utils4s.spark.dataframe

/**
  * Created by xbsu on 16/1/18.
  */
object SparkDataFrameUDFApp extends SparkSQLSupport{
  def main(args: Array[String]) {
    val path = "spark-dataframe-demo/src/main/resources/b.txt"
    createTable(path, "people", "age name", f)
    hiveContext.sql("SELECT age,name FROM people").show()
    //UDF测试
    //更详细解释：http://zhangyi.farbox.com/post/kai-yuan-kuang-jia/udf-and-udaf-in-spark
    hiveContext.udf.register("getSourceType", getSourceType(_: String))
    hiveContext.sql("SELECT age,getSourceType(name) FROM people").show()
  }
  /**
    * UDF验证
    * @param remark
    * @return
    */
  def getSourceType(remark: String): Int = {
    val typePattern = "yzt_web|iphone|IPHONE|ANDROID".r
    val logType = typePattern.findFirstIn(remark).getOrElse("")

    logType match {
      case "yzt_web" ⇒ 0
      case "ANDROID" ⇒ 1
      case "IPHONE" ⇒ 2
      case "iphone" ⇒ 2
      case _ ⇒ 404
    }
  }
}
