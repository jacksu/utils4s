package cn.thinkjoy.utils4s.spark.dataframe

import cn.thinkjoy.utils4s.spark.dataframe.SparkDataFrameApp._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{ StringType, StructField, StructType }
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{ SparkContext, SparkConf }

/**
 * Created by xbsu on 16/1/18.
 */

//TODO 1.5.2 初步猜想hive依赖的环境是1.4.0导致的，后面需要验证，
//HiveContext继承SQLContext，现在需要测试1.5.2新增支持的函数

class SparkSQLSupport(val appName: String, val master: String = "local") {
  @transient
  val conf = new SparkConf().setAppName(appName).setMaster(master)
  @transient
  val sc = new SparkContext(conf)

  val hiveContext = new HiveContext(sc)

  val sqlContext = new SQLContext(sc)

  /**
   * 通过hdfs文件建表
   * @param path         文件所在路径
   * @param table        注册表名
   * @param schemaString 表的schema
   * @param f            内容转化函数
   */
  def createTableFromStr(
    path: String,
    table: String,
    schemaString: String,
    f: RDD[String] ⇒ RDD[Row]): DataFrame = {

    val people = sc.textFile(path)
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName ⇒ StructField(fieldName, StringType, true)))

    // Convert records of the RDD (people) to Rows.
    //val rowRDD = people.map(_.split(",")).map(p => Row(p(0), p(1).trim))
    val rowRDD = f(people)

    // Apply the schema to the RDD.
    val peopleSchemaRDD = sqlContext.createDataFrame(rowRDD, schema)

    // Register the SchemaRDD as a table.
    peopleSchemaRDD.registerTempTable(table)

    peopleSchemaRDD
  }

  /**
   * 经过测试不需要指定schema，默认会补全字段
   * @param path
   * @param table
   */
  def createTableFromJson(
    path: String,
    table: String): Unit = {

    val peopleSchemaRDD = sqlContext.read.json(path)

    // Register the SchemaRDD as a table.
    peopleSchemaRDD.registerTempTable(table)
  }

}