#spark实践总结

##尽量少使用groupByKey

[**测试源码**](https://github.com/jacksu/utils4s/blob/master/spark-core-demo/src/main/scala/cn/thinkjoy/utils4s/spark/core/GroupByKeyAndReduceByKeyApp.scala)

下面来看看groupByKey和reduceByKey的区别：

```scala   
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
```
虽然两个函数都能得出正确的结果， 但reduceByKey函数更适合使用在大数据集上。 这是因为Spark知道它可以在每个分区移动数据之前将输出数据与一个共用的`key`结合。

借助下图可以理解在reduceByKey里发生了什么。 在数据对被搬移前，同一机器上同样的`key`是怎样被组合的( reduceByKey中的 lamdba 函数)。然后 lamdba 函数在每个分区上被再次调用来将所有值 reduce成最终结果。整个过程如下：

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/reduceByKey.png)

另一方面，当调用 groupByKey时，所有的键值对(key-value pair) 都会被移动,在网络上传输这些数据非常没必要，因此避免使用 GroupByKey。

为了确定将数据对移到哪个主机，Spark会对数据对的`key`调用一个分区算法。 当移动的数据量大于单台执行机器内存总量时`Spark`会把数据保存到磁盘上。 不过在保存时每次会处理一个`key`的数据，所以当单个 key 的键值对超过内存容量会存在内存溢出的异常。 这将会在之后发行的 Spark 版本中更加优雅地处理，这样的工作还可以继续完善。 尽管如此，仍应避免将数据保存到磁盘上，这会严重影响性能。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/groupByKey.png)

你可以想象一个非常大的数据集，在使用 reduceByKey 和 groupByKey 时他们的差别会被放大更多倍。

我们来看看两个函数的实现：

```scala
  def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)] = self.withScope {
    combineByKeyWithClassTag[V]((v: V) => v, func, func, partitioner)
  }
```

```scala
  /**
  * Note: As currently implemented, groupByKey must be able to hold all the key-value pairs for any
   * key in memory. If a key has too many values, it can result in an [[OutOfMemoryError]].
   */
  def groupByKey(partitioner: Partitioner): RDD[(K, Iterable[V])] = self.withScope {
    // groupByKey shouldn't use map side combine because map side combine does not
    // reduce the amount of data shuffled and requires all map side data be inserted
    // into a hash table, leading to more objects in the old gen.
    val createCombiner = (v: V) => CompactBuffer(v)
    val mergeValue = (buf: CompactBuffer[V], v: V) => buf += v
    val mergeCombiners = (c1: CompactBuffer[V], c2: CompactBuffer[V]) => c1 ++= c2
    val bufs = combineByKeyWithClassTag[CompactBuffer[V]](
      createCombiner, mergeValue, mergeCombiners, partitioner, mapSideCombine = false)
    bufs.asInstanceOf[RDD[(K, Iterable[V])]]
  }
```

**注意`mapSideCombine=false`,partitioner是`HashPartitioner`**，但是groupByKey对小数据量比较好，一个key对应的个数少于10个。

他们都调用了`combineByKeyWithClassTag`，我们再来看看`combineByKeyWithClassTag`的定义：

```scala
  def combineByKeyWithClassTag[C](
      createCombiner: V => C,
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C,
      partitioner: Partitioner,
      mapSideCombine: Boolean = true,
      serializer: Serializer = null)(implicit ct: ClassTag[C]): RDD[(K, C)]
```

combineByKey函数主要接受了三个函数作为参数，分别为createCombiner、mergeValue、mergeCombiners。这三个函数足以说明它究竟做了什么。理解了这三个函数，就可以很好地理解combineByKey。

combineByKey是将RDD[(K,V)]combine为RDD[(K,C)]，因此，首先需要提供一个函数，能够完成从V到C的combine，称之为combiner。如果V和C类型一致，则函数为V => V。倘若C是一个集合，例如Iterable[V]，则createCombiner为V => Iterable[V]。

mergeValue则是将原RDD中Pair的Value合并为操作后的C类型数据。合并操作的实现决定了结果的运算方式。所以，mergeValue更像是声明了一种合并方式，它是由整个combine运算的结果来导向的。函数的输入为原RDD中Pair的V，输出为结果RDD中Pair的C。

最后的mergeCombiners则会根据每个Key所对应的多个C，进行归并。

例如：

```scala
var rdd1 = sc.makeRDD(Array(("A", 1), ("A", 2), ("B", 1), ("B", 2),("B",3),("B",4), ("C", 1)))
    rdd1.combineByKey(
      (v: Int) => v + "_",
      (c: String, v: Int) => c + "@" + v,
      (c1: String, c2: String) => c1 + "$" + c2
    ).collect.foreach(println)
```

result不确定欧，单机执行不会调用mergeCombiners：

```scala
(B,1_@2@3@4)
(A,1_@2)
(C,1_)
```
在集群情况下：

```scala
(B,2_@3@4$1_)
(A,1_@2)
(C,1_)
或者
(B,1_$2_@3@4)
(A,1_@2)
(C,1_)

```

`mapSideCombine=false`时，再体验一下运行结果。

有许多函数比goupByKey好：

1. 当你combine元素时，可以使用`combineByKey`，但是输入值类型和输出可能不一样
2. `foldByKey`合并每一个 key 的所有值，在级联函数和“零值”中使用。

```scala
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
```

`foldByKey`,`aggregateByKey`都是由combineByKey实现，并且`mapSideCombine=true`，因此可以使用这些函数替代goupByKey。

###参考
[Spark中的combineByKey](http://zhangyi.farbox.com/post/kai-yuan-kuang-jia/combinebykey-in-spark )

[databricks gitbooks](https://databricks.gitbooks.io/databricks-spark-knowledge-base/content/best_practices/prefer_reducebykey_over_groupbykey.html)

[在Spark中尽量少使用GroupByKey函数](http://www.iteblog.com/archives/1357)