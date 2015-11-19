package cn.thinkjoy.utils4s.sparkstreaming

import _root_.kafka.serializer.StringDecoder
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by jacksu on 15/11/12.
 */

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>
 * <zkQuorum> is a list of one or more zookeeper servers that make quorum
 * <group> is the name of kafka consumer group
 * <topics> is a list of one or more kafka topics to consume from
 * <numThreads> is the number of threads the kafka consumer should use
 *
 * Example:
 * `$ bin/run-example \
 * org.apache.spark.examples.streaming.KafkaWordCount zoo01,zoo02,zoo03 \
 * my-consumer-group topic1,topic2 1`
 */
object SparkStreamingDemo {

  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> <batch>")
      System.exit(1)
    }

    val Array(zkQuorum, group, topics, numThreads,batch) = args
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
    (1 to receiveNum).map(_ => {
      val lines = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topicMap, StorageLevel.MEMORY_AND_DISK_SER_2)
      lines.map(_._2).flatMap(_.split(" ")).map(x => (x, 1L))
        .reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2).print
    }
    )

    //开始计算
    ssc.start()
    //等待计算结束
    ssc.awaitTermination()
  }

}

