package cn.thinkjoy.utils4s.sparkstreaming

import kafka.serializer.StringDecoder
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by jacksu on 16/1/4.
 */
object SparkStreamingDataFrameDemo {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> <batch>")
      System.exit(1)
    }

    val Array(zkQuorum, group, topics, numThreads, batch) = args
    val sparkConf = new SparkConf().setAppName("KafkaWordCount")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(batch.toInt))
    ssc.checkpoint("checkpoint")

    //numThreads 处理每个topic的线程数
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val kafkaParams = Map[String, String](
      "zookeeper.connect" -> zkQuorum, "group.id" -> group,
      "zookeeper.connection.timeout.ms" -> "10000",
      //auto.offset.reset设置为smallest，不然启动的时候为largest，只能收取实时消息
      "auto.offset.reset" -> "smallest"
    )
    //一般由两个以上接收线程，防止一个线程失败，但此处会分别统计
    val receiveNum = 2
    val lines = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicMap, StorageLevel.MEMORY_AND_DISK_SER_2)
    lines.map(_._2).flatMap(_.split(" ")).foreachRDD(rdd => {
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._
      val wordsDF = rdd.toDF("word")
      wordsDF.registerTempTable("words")
      val wordsCount = sqlContext.sql("select word,count(*) from words group by word")
      wordsCount.show()
    })


    //开始计算
    ssc.start()
    //等待计算结束
    ssc.awaitTermination()
  }
}
