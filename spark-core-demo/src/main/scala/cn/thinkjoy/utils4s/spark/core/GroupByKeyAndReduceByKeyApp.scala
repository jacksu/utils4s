package cn.thinkjoy.utils4s.spark.core

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}


object GroupByKeyAndReduceByKeyApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("GroupAndReduce").setMaster("local")
    val sc = new SparkContext(conf)
    val words = Array("one", "two", "two", "three", "three", "three")
    val wordsRDD = sc.parallelize(words)

    val wordsCountWithReduce = wordsRDD.
      map(word => (word, 1)).
      reduceByKey(_ + _).
      collect().
      foreach(println)

    val wordsCountWithGroup = wordsRDD.
      map(word => (word, 1)).
      groupByKey().
      map(w => (w._1, w._2.sum)).
      collect().
      foreach(println)

    //使用combineByKey计算wordcount
    wordsRDD.map(word=>(word,1)).combineByKey(
      (v: Int) => v,
      (c: Int, v: Int) => c+v,
      (c1: Int, c2: Int) => c1 + c2
    ).collect.foreach(println)

    //使用foldByKey计算wordcount
    println("=======foldByKey=========")
    wordsRDD.map(word=>(word,1)).foldByKey(0)(_+_).foreach(println)

    //使用aggregateByKey计算wordcount
    println("=======aggregateByKey============")
    wordsRDD.map(word=>(word,1)).aggregateByKey(0)((u:Int,v)=>u+v,_+_).foreach(println)

    var rdd1 = sc.makeRDD(Array(("A", 1), ("A", 2), ("B", 1), ("B", 2), ("B", 3), ("B", 4), ("C", 1)))
    rdd1.combineByKey(
      (v: Int) => v + "_",
      (c: String, v: Int) => c + "@" + v,
      (c1: String, c2: String) => c1 + "$" + c2,
      new HashPartitioner(2),
      mapSideCombine = false
    ).collect.foreach(println)
  }
}
