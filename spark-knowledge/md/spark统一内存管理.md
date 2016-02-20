#spark统一内存管理

spark从1.6.0开始内存管理发生了变化，原来的内存管理由[StaticMemoryManager](https://github.com/apache/spark/blob/branch-1.6/core/src/main/scala/org/apache/spark/memory/StaticMemoryManager.scala)实现，现在被称为`Legacy`,在1.5.x和1.6.0中运行相同代码的行为是不同的，为了兼容`Legacy`,可以通过`spark.memory.useLegacyMode`来设置，默认该参数是关闭的。

前面有一篇介绍spark内存管理的文章[spark内存概述](http://www.jianshu.com/p/f0f28af4bd83),现在介绍1.6.0的内存管理，由[UnifiedMemoryManager](https://github.com/apache/spark/blob/branch-1.6/core/src/main/scala/org/apache/spark/memory/UnifiedMemoryManager.scala)实现。

1.6.0的统一内存管理如下：

![Spark-Memory-Management-1.6.0](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/Spark-Memory-Management-1.6.0.png)

主要有三部分组成：

**1 Reserved Memory**

这部分内存是预留给**系统**使用，是固定不变的。在1.6.0默认为300MB(`RESERVED_SYSTEM_MEMORY_BYTES = 300 * 1024 * 1024`)，这一部分内存不计算在spark execution和storage中，除了重新编译spark和` spark.testing.reservedMemory`,Reserved Memory是不可以改变的，` spark.testing.reservedMemory`不推荐使用在实际运行环境中。是用来存储Spark internal objects，并且限制JVM的大小，如果executor的大小小于1.5 * Reserved Memory = 450MB ，那么就会报 “please use larger heap size”的错误,源码如下。

```scala
	val minSystemMemory = reservedMemory * 1.5
    if (systemMemory < minSystemMemory) {
      throw new IllegalArgumentException(s"System memory $systemMemory must " +
        s"be at least $minSystemMemory. Please use a larger heap size.")
    }
```

**2 User Memory**

分配**Spark Memory**剩余的内存，用户可以根据需要使用。可以存储`RDD transformations`需要的数据结构，例如，	重写`spark aggregation`,使用`mapPartition transformation`，通过`hash table`来实现`aggregation`，这样使用的就是`User Memory`。在1.6.0中，计算方法为**`(“Java Heap” – “Reserved Memory”) * (1.0 – spark.memory.fraction)`**，默认为**` (“Java Heap” – 300MB) * 0.25`**，比如4GB的heap大小，那么`User Memory`的大小为949MB。由用户来决定存储的数据量，因此要遵守这个边界，不然会导致OOM。


**3 Spark Memory**

计算方式是**`(“Java Heap” – “Reserved Memory”) * spark.memory.fraction`**，在1.6.0中，默认为**` (“Java Heap” – 300MB) * 0.75`**。例如推的大小为4GB，那么`Spark Memory`为2847MB。`Spark Memory`又分为`Storage Memory`和`Execution Memory`两部分。两个边界由`spark.memory.storageFraction`设定，默认为0.5。但是两部分可以动态变化，相互之间可以借用，如果一方使用完，可以向另一方借用。先看看两部分是如何使用的。

* > **Storage Memory** 用来存储`spark cached data`也可作为临时空间存储序列化`unroll`，`broadcast variables`作为`cached block`存储，但是需要注意，这是[unroll](https://github.com/apache/spark/blob/branch-1.6/core/src/main/scala/org/apache/spark/storage/MemoryStore.scala#L249)源码，`unrolled block`如果内存不够，会存储在`driver`端。`broadcast variables`大部分存储级别为`MEMORY_AND_DISK`。

* > **Execution Memory** 存储Spark task执行过程中需要的对象，例如，Shuffle中map端中间数据的存储，以及hash aggregation中的hash table。如果内存不足，该空间也容许spill到磁盘。

`Execution Memory`不可以淘汰block，不然执行的时候就会fail，如果找不到block。`Storage Memory`中的内容可以淘汰。`Execution Memory`满足两种情况可以向`Storage Memory`借用空间：

1. `Storage Memory`还有free空间

2. `Storage Memory`大于初始化时的空间(`"Spark Memory" * spark.memory.storageFraction = (“Java Heap” – “Reserved Memory”) * spark.memory.fraction * spark.memory.storageFraction`)

`Storage Memory`只有在`Execution Memory`有free空间时，才可以借用。

##参考

[spark memory management](http://0x0fff.com/spark-memory-management/)

[Spark Broadcast](http://www.kancloud.cn/kancloud/spark-internals/45238)