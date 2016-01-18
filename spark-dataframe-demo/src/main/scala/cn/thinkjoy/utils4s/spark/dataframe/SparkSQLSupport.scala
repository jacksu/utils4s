package cn.thinkjoy.utils4s.spark.dataframe

import cn.thinkjoy.utils4s.spark.dataframe.SparkDataFrameApp._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by xbsu on 16/1/18.
  */
class SparkSQLSupport {
  @transient
  val conf = new SparkConf().setAppName("test").setMaster("local")
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
  def createTable(
    path: String,
    table: String,
    schemaString: String,
    f: RDD[String] ⇒ RDD[Row]): Unit = {

    val people = sc.textFile(path)
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName ⇒ StructField(fieldName, StringType, true)))

    // Convert records of the RDD (people) to Rows.
    //val rowRDD = people.map(_.split(",")).map(p => Row(p(0), p(1).trim))
    val rowRDD = f(people)

    // Apply the schema to the RDD.
    val peopleSchemaRDD = hiveContext.createDataFrame(rowRDD, schema)

    // Register the SchemaRDD as a table.
    peopleSchemaRDD.registerTempTable(table)
  }

  def createTableFromJson(
    path: String,
    table: String,
    schemaString: String): Unit = {

    val schema =
      StructType(
        schemaString.split(" ").map(fieldName ⇒ StructField(fieldName, StringType, true)))

    val peopleSchemaRDD = hiveContext.read.schema(schema).json(path)

    // Register the SchemaRDD as a table.
    peopleSchemaRDD.registerTempTable(table)
  }

  /**
    * 对输入的内容转化为Row
    * @param line
    * @return
    */
  def f(line: RDD[String]): RDD[Row] = {
    line.map(_.split(" ")).map(array ⇒ Row(array(0), array(1)))
  }
}
