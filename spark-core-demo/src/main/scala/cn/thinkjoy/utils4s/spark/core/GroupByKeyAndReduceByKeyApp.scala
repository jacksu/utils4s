package cn.thinkjoy.utils4s.spark.core

import org.apache.spark.{SparkConf, SparkContext}


object GroupByKeyAndReduceByKeyApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("GroupAndReduce").setMaster("local")
    val sc = new SparkContext(conf)
    val words = Array("one", "two", "two", "three", "three", "three")
    val wordsRDD = sc.parallelize(words).map(word => (word, 1))
    val wordsCountWithReduce = wordsRDD.
      reduceByKey(_ + _).
      collect().
      foreach(println)
    val wordsCountWithGroup = wordsRDD.
      groupByKey().
      map(w => (w._1, w._2.sum)).
      collect().
      foreach(println)

    wordsRDD.combineByKey(_+_)
  }
}
