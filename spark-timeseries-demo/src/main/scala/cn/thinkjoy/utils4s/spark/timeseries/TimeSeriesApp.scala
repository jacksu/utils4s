package cn.thinkjoy.utils4s.spark.timeseries

import java.sql.Timestamp

import com.cloudera.sparkts._
import com.cloudera.sparkts.stats.TimeSeriesStatisticalTests
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.types._
import org.joda.time._
import com.cloudera.sparkts.models.Autoregression

/**
 * jacksu
 */

object TimeSeriesApp {

  /**
   * Creates a Spark DataFrame of (timestamp, symbol, price) from a tab-separated file of stock
   * ticker data.
   */
  def loadObservations(sqlContext: SQLContext, path: String): DataFrame = {
    val rowRdd = sqlContext.sparkContext.textFile(path).map { line =>
      val tokens = line.split('\t')
      val dt = new DateTime(tokens(0).toInt, tokens(1).toInt, tokens(2).toInt, 0, 0)
      val symbol = tokens(3)
      val price = tokens(4).toDouble
      Row(new Timestamp(dt.getMillis), symbol, price)
    }
    val fields = Seq(
      StructField("timestamp", TimestampType, true),
      StructField("symbol", StringType, true),
      StructField("price", DoubleType, true)
    )
    val schema = StructType(fields)
    sqlContext.createDataFrame(rowRdd, schema)
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark-TS Wiki Example").setMaster("local")
    conf.set("spark.io.compression.codec", "org.apache.spark.io.LZ4CompressionCodec")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val tickerObs = loadObservations(sqlContext, "spark-timeseries-demo/data/ticker.tsv")

    // Create an daily DateTimeIndex over August and September 2015
    val dtIndex = DateTimeIndex.uniform(
      new DateTime("2015-08-03"), new DateTime("2015-09-22"), new BusinessDayFrequency(1))

    // Align the ticker data on the DateTimeIndex to create a TimeSeriesRDD
    val tickerTsrdd = TimeSeriesRDD.timeSeriesRDDFromObservations(dtIndex, tickerObs,
      "timestamp", "symbol", "price")

    // Cache it in memory
    tickerTsrdd.cache()

    // Count the number of series (number of symbols)
    println("======"+tickerTsrdd.count()+"=======")

    // Impute missing values using linear interpolation
    val filled = tickerTsrdd.fill("linear")

    // Compute return rates 计算回报率
    val returnRates = filled.returnRates()

    // Compute Durbin-Watson stats for each series
    val dwStats = returnRates.mapValues(TimeSeriesStatisticalTests.dwtest(_))

    println(dwStats.map(_.swap).min)
    println(dwStats.map(_.swap).max)
  }


}
