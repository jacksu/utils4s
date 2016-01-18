package cn.thinkjoy.utils4s.spark.dataframe

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.{ SparkContext, SparkConf }

/**
 * Created by jack on 15-12-10.
 */

object SparkDataFrameApp extends SparkSQLSupport{

  def main(args: Array[String]) {
    //txt通用创建表测试
    val path = "spark-dataframe-demo/src/main/resources/b.txt"
    createTable(path, "people", "age name", f)
    hiveContext.sql("SELECT age,name FROM people").show()

    //json测试
    val test = hiveContext.read.json("spark-dataframe-demo/src/main/resources/a.json")
    test.printSchema()
    test.cache()
    test.registerTempTable("test")
    hiveContext.sql("SELECT name.first FROM test").show()

    //parquet测试

    test.write.parquet("spark-dataframe-demo/src/main/resources/parquet")
    val parquet = hiveContext.read.parquet("spark-dataframe-demo/src/main/resources/parquet")
    parquet.registerTempTable("parquet")
    hiveContext.sql("select * from parquet").collect().foreach(println)

  }



}
