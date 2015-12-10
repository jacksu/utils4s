package cn.thinkjoy.utils4s.spark.dataframe

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkContext, SparkConf}


object SparkDataFrameApp  {
  def main(args: Array[String]) {
    @transient
    val conf = new SparkConf().setAppName("test").setMaster("local")
    @transient
    val sc = new SparkContext(conf)

    @transient
    val hiveContext = new HiveContext(sc)
    val file=sc.textFile("b.txt")
    import hiveContext.implicits._
    file.flatMap(line=>line.toString().split("\t")).
      toDF("age","name").show()

  }
}
