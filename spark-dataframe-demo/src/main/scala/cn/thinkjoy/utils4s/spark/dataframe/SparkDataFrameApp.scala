package cn.thinkjoy.utils4s.spark.dataframe

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.{ SparkContext, SparkConf }

/**
 * Created by jack on 15-12-10.
 */

object SparkDataFrameApp extends SparkSQLSupport("DataFrameApp") {

  def main(args: Array[String]) {
    //txt通用创建表测试
    val path = "spark-dataframe-demo/src/main/resources/b.txt"
    createTableFromStr(path, "people", "age name", f)
    sqlContext.sql("SELECT age,name FROM people").show()

    //json测试
    createTableFromJson("spark-dataframe-demo/src/main/resources/a.json",
      "test")
    sqlContext.sql("SELECT age,name.first FROM test").show()

    //parquet测试
    val test = sqlContext.read.json("spark-dataframe-demo/src/main/resources/a.json");
    test.write.parquet("spark-dataframe-demo/src/main/resources/parquet")
    val parquet = sqlContext.read.parquet("spark-dataframe-demo/src/main/resources/parquet")
    parquet.registerTempTable("parquet")
    sqlContext.sql("select * from parquet").collect().foreach(println)

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
