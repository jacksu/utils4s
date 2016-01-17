#spark实践总结
##避免使用groupByKey

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

在大数据情况下，reduceByKey更好，因为reduceByKey在shuffling前，在每个partition中，相同的key会combine。看下面的图，相同机器相同key的数据会combine，在shuffle前通过reduceByKey中传入的函数，在reduce端再次调用产生最终结果。


![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/reduceByKey.png)

groupByKey,key-value对没有combine在网络中传输，如果数据比较大，还可能需要spill到disk，非常影响性能。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/groupByKey.png)

有许多函数比goupByKey好：

1. 当你combine元素时，可以使用combineByKey，但是输入值类型和输出不一样
2. 