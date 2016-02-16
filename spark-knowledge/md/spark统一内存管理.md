#spark统一内存管理

spark从1.6.0开始内存管理发生了变化，原来的内存管理由[StaticMemoryManager](https://github.com/apache/spark/blob/branch-1.6/core/src/main/scala/org/apache/spark/memory/StaticMemoryManager.scala)实现，现在被称为`Legacy`,在1.5.x和1.6.0中运行相同代码的行为是不同的，为了兼容`Legacy`,可以通过`spark.memory.useLegacyMode`来设置，默认该参数是关闭的。

前面有一篇介绍spark内存管理的文章[spark内存概述](http://www.jianshu.com/p/f0f28af4bd83),现在介绍1.6.0的内存管理，由[UnifiedMemoryManager](https://github.com/apache/spark/blob/branch-1.6/core/src/main/scala/org/apache/spark/memory/UnifiedMemoryManager.scala)实现。

1.6.0的统一内存管理如下：

![Spark-Memory-Management-1.6.0](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/Spark-Memory-Management-1.6.0.png)

主要有三部分组成：

1 **Reserved Memory**

这部分内存是预留给**系统**使用，是固定不变的。


##参考

[spark memory management](http://0x0fff.com/spark-memory-management/)