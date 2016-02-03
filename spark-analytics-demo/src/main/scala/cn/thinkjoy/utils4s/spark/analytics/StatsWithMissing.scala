package cn.thinkjoy.utils4s.spark.analytics

import org.apache.spark.rdd.RDD

/**
 * Created by jack on 16/1/31.
 */
package object StatsWithMissing {
  /**
   * Double数组数据统计
   * @param rdd
   * @return
   */
  def statsWithMissing(rdd: RDD[Array[Double]]): Array[NAStatCounter] = {
    val nastats = rdd.mapPartitions((iter: Iterator[Array[Double]]) => {
      val nas: Array[NAStatCounter] = iter.next().map(d => NAStatCounter(d))

      iter.foreach(arr => {
        nas.zip(arr).foreach { case (n, d) => n.add(d) }
      })
      Iterator(nas)
    })
    nastats.reduce((n1, n2) => {
      n1.zip(n2).map { case (a, b) => a.merge(b) }
    })
  }
}

