package cn.thinkjoy.utils4s.spark.dataframe

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by jack on 15-12-10.
 */


object SparkDataFrameApp {
  @transient
  val conf = new SparkConf().setAppName("test").setMaster("local")
  @transient
  val sc = new SparkContext(conf)

  val hiveContext = new HiveContext(sc)

  def main(args: Array[String]) {
    //通用创建表测试
    val path = "spark-dataframe-demo/src/main/resources/b.txt"
    createTable(path, "people", "age name", f)
    hiveContext.sql("SELECT age,name FROM people").show()

    //UDF测试
    hiveContext.udf.register("getSourceType",getSourceType(_:String))
    hiveContext.sql("SELECT age,getSourceType(name) FROM people").show()

    //json测试
    val test=hiveContext.read.json("spark-dataframe-demo/src/main/resources/a.json")
    test.printSchema()
    test.registerTempTable("test")
    hiveContext.sql("SELECT name.first FROM test").show()
  }

  /**
   * 对输入的内容转化为Row
   * @param line
   * @return
   */
  def f(line: RDD[String]): RDD[Row] = {
    line.map(_.split(" ")).map(array => Row(array(0), array(1)))
  }

  /**
   * 通过hdfs文件建表
   * @param path         文件所在路径
   * @param table        注册表名
   * @param schemaString 表的schema
   * @param f            内容转化函数
   */
  def createTable(
    path: String,
    table: String,
    schemaString: String,
    f: RDD[String] => RDD[Row]): Unit = {

    val people = sc.textFile(path)
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    // Convert records of the RDD (people) to Rows.
    //val rowRDD = people.map(_.split(",")).map(p => Row(p(0), p(1).trim))
    val rowRDD = f(people)

    // Apply the schema to the RDD.
    val peopleSchemaRDD = hiveContext.createDataFrame(rowRDD, schema)

    // Register the SchemaRDD as a table.
    peopleSchemaRDD.registerTempTable(table)
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
      case "yzt_web" => 0
      case "ANDROID" => 1
      case "IPHONE" => 2
      case "iphone" => 2
      case _ => 404
    }
  }
}
