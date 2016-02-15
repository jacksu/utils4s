package cn.thinkjoy.utils4s.spark.dataframe

import java.sql.Timestamp
import java.sql.Date

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql._

/**
  * 参考：http://zhangyi.farbox.com/post/kai-yuan-kuang-jia/rollup-in-spark
  * 数据pivot，比如统计商品四个季度的销售量，可以参考：https://databricks.com/blog/2016/02/09/reshaping-data-with-pivot-in-spark.html
  * Created by xbsu on 16/1/18.
  */
object RollupApp {

  implicit class StringFuncs(str: String) {
    def toTimestamp = new Timestamp(Date.valueOf(str).getTime)
  }

  def main(args: Array[String]) {
    @transient
    val conf = new SparkConf().setAppName("test").setMaster("local")

    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._
    val sales = Seq(
      (1, "Widget Co", 1000.00, 0.00, "广东省", "深圳市", "2014-02-01".toTimestamp),
      (2, "Acme Widgets", 1000.00, 500.00, "四川省", "成都市", "2014-02-11".toTimestamp),
      (3, "Acme Widgets", 1000.00, 500.00, "四川省", "绵阳市", "2014-02-12".toTimestamp),
      (4, "Acme Widgets", 1000.00, 500.00, "四川省", "成都市", "2014-02-13".toTimestamp),
      (5, "Widget Co", 1000.00, 0.00, "广东省", "广州市", "2015-01-01".toTimestamp),
      (6, "Acme Widgets", 1000.00, 500.00, "四川省", "泸州市", "2015-01-11".toTimestamp),
      (7, "Widgetry", 1000.00, 200.00, "四川省", "成都市", "2015-02-11".toTimestamp),
      (8, "Widgets R Us", 3000.00, 0.0, "四川省", "绵阳市", "2015-02-19".toTimestamp),
      (9, "Widgets R Us", 2000.00, 0.0, "广东省", "深圳市", "2015-02-20".toTimestamp),
      (10, "Ye Olde Widgete", 3000.00, 0.0, "广东省", "深圳市", "2015-02-28".toTimestamp),
      (11, "Ye Olde Widgete", 3000.00, 0.0, "广东省", "广州市", "2015-02-28".toTimestamp))

    val saleDF = sqlContext.sparkContext.parallelize(sales, 4).toDF("id", "name", "sales", "discount", "province", "city", "saleDate")
    saleDF.registerTempTable("sales")

    val dataFrame = sqlContext.sql("select province,city,sales from sales")
    dataFrame.show

    val resultDF = dataFrame.rollup($"province", $"city").agg(Map("sales" -> "sum"))
    resultDF.show

    //可以通过groupBy实现rollup
    dataFrame.groupBy("province", "city").agg(Map("sales" -> "sum")).show()
  }
}
