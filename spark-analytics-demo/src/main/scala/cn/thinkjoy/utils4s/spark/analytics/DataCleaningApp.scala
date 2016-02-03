package cn.thinkjoy.utils4s.spark.analytics

import org.apache.spark.{SparkContext, SparkConf}
import StatsWithMissing._

/**
 * Created by jacksu on 16/1/27.
 */
case class MatchData(id1: Int, id2: Int,
                     scores: Array[Double], matched: Boolean)

case class Scored(md: MatchData, score: Double)

object DataCleaningApp {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Dataleaning").setMaster("local")
    val sc = new SparkContext(conf)
    val noheader = sc.textFile("spark-analytics-demo/src/main/resources/block_1.csv").filter(!isHeader(_))

    val parsed = noheader.map(parse)
    //为了验证文件加载是否正确
    //println(parsed.first())
    //如果数据需要多次处理，就使用cache
    parsed.cache()

    val matchCounts = parsed.map(md => md.matched).countByValue()
    //Map不可以排序，只能转化为Seq
    val matchCountsSeq = matchCounts.toSeq
    matchCountsSeq.sortBy(_._2).reverse.foreach(println)

    val stats = (0 until 9).map(i => {
      parsed.map(_.scores(i)).filter(!_.isNaN).stats()
    })
    stats.foreach(println)

    //测试NAStatCounter
    val nas1 = NAStatCounter(10.0)
    nas1.add(2.1)
    val nas2 = NAStatCounter(Double.NaN)
    nas1.merge(nas2)
    println(nas1.toString)
    val nasRDD = parsed.map(md => {
      md.scores.map(d => NAStatCounter(d))
    })
    val reduced = nasRDD.reduce((n1, n2) => {
      n1.zip(n2).map { case (a, b) => a.merge(b) }
    })
    reduced.foreach(println)

    statsWithMissing(parsed.filter(_.matched).map(_.scores)).foreach(println)

  }

  /**
   * 判断是不是头
   * @param line
   * @return
   */
  def isHeader(line: String) = line.contains("id_1")

  /**
   * 字符串转化为double
   * @param s
   * @return
   */
  def toDouble(s: String) = {
    if ("?".equals(s)) Double.NaN else s.toDouble
  }

  /**
   * 解析每一行,用case class表示
   * @param line
   * @return
   */
  def parse(line: String) = {
    val pieces = line.split(',')
    val id1 = pieces(0).toInt
    val id2 = pieces(1).toInt
    val scores = pieces.slice(2, 11).map(toDouble _)
    val matched = pieces(11).toBoolean
    MatchData(id1, id2, scores, matched)
  }
}
