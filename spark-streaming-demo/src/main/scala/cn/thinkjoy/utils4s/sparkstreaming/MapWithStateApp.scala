package cn.thinkjoy.utils4s.sparkstreaming

import scala.util.Random

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.storage._
import org.apache.spark.streaming.receiver.Receiver

/**
  * 1.6中mapWitchState的测试
  * databricks的测试用例：https://docs.cloud.databricks.com/docs/spark/1.6/examples/Streaming%20mapWithState.html
  * databricks文章介绍：https://databricks.com/blog/2016/02/01/faster-stateful-stream-processing-in-spark-streaming.html
  * Created by xbsu on 16/2/3.
  */

class DummySource(ratePerSec: Int) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) {

  def onStart() {
    // Start the thread that receives data over a connection
    new Thread("Dummy Source") {
      override def run() {
        receive()
      }
    }.start()
  }

  def onStop() {
    // There is nothing much to do as the thread calling receive()
    // is designed to stop by itself isStopped() returns false
  }

  /** Create a socket connection and receive data until receiver is stopped */
  private def receive() {
    while (!isStopped()) {
      store("I am a dummy source " + Random.nextInt(10))
      Thread.sleep((1000.toDouble / ratePerSec).toInt)
    }
  }
}

object MapWithStateApp {
  def main(args: Array[String]) {

    val sparkConf = new SparkConf().setAppName("mapWithState").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val batchIntervalSeconds = 2
    val eventsPerSecond = 10
    // Create a StreamingContext
    val ssc = new StreamingContext(sc, Seconds(batchIntervalSeconds))

    // Create a stream that generates 1000 lines per second
    val stream = ssc.receiverStream(new DummySource(eventsPerSecond))

    // Split the lines into words, and create a paired (key-value) dstream
    val wordStream = stream.flatMap {
      _.split(" ")
    }.map(word => (word, 1))

    val initialRDD = sc.parallelize(List(("dummy", 100L), ("source", 32L)))
    val stateSpec = StateSpec.function(trackStateFunc _)
      .initialState(initialRDD)
      .numPartitions(2)
      .timeout(Seconds(60))

    // This represents the emitted stream from the trackStateFunc. Since we emit every input record with the updated value,
    // this stream will contain the same # of records as the input dstream.
    val wordCountStateStream = wordStream.mapWithState(stateSpec)
    wordCountStateStream.print()

    // A snapshot of the state for the current batch. This dstream contains one entry per key.
    val stateSnapshotStream = wordCountStateStream.stateSnapshots()
    stateSnapshotStream.print()
    //stateSnapshotStream.foreachRDD { rdd =>
    //  rdd.toDF("word", "count").registerTempTable("batch_word_count")
    //}

    // To make sure data is not deleted by the time we query it interactively
    //ssc.remember(Minutes(1))

    ssc.checkpoint("checkpoint")

    // Start the streaming context in the background.
    ssc.start()

    // This is to ensure that we wait for some time before the background streaming job starts.
    // This will put this cell on hold for 5 times the batchIntervalSeconds.
    ssc.awaitTerminationOrTimeout(batchIntervalSeconds * 2 * 1000)
  }

  /**
  * In this example:
  * - key is the word.
  * - value is '1'. Its type is 'Int'.
  * - state has the running count of the word. It's type is Long. The user can provide more custom classes as type too.
  * - The return value is the new (key, value) pair where value is the updated count.
  */

  def trackStateFunc(key: String, value: Option[Int], state: State[Long]): Option[(String, Long)] = {
    val sum = value.getOrElse(0).toLong + state.getOption.getOrElse(0L)
    val output = (key, sum)
    state.update(sum)
    Some(output)
  }

}
