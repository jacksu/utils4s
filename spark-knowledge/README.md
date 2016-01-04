#spark内存概述

spark 1.6发布了新的统一内存管理，TODO新内存概述。

spark进程是以JVM进程运行的，可以通过-Xmx和-Xms配置堆栈大小，它是如何使用堆栈呢？下面是spark内存分配图。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/Spark-Heap-Usage.png)

## storage memory
spark默认JVM堆为512MB，为了避免OOM错误，只使用90%。通过spark.storage.safetyFraction来设置。spark通过内存来存储需要处理的数据，使用安全空间的60%，通过 spark.storage.memoryFraction来控制。如果我们想知道spark缓存数据可以使用多少空间？假设执行任务需要executors数为N，那么可使用空间为N\*90%\*60%\*512MB，但实际缓存数据的空间还要减去unroll memory。
##shuffle memory
shuffle memory的内存为“Heap Size” \* spark.shuffle.safetyFraction \* spark.shuffle.memoryFraction。默认spark.shuffle.safetyFraction 是 0.8 ，spark.shuffle.memoryFraction是0.2 ，因此shuffle memory为 0.8\*0.2\*512MB = 0.16\*512MB，shuffle memory为shuffle用作数据的排序等。
##unroll memory
unroll memory的内存为spark.storage.unrollFraction \* spark.storage.memoryFraction \* spark.storage.safetyFraction，即0.2 \* 0.6 \* 0.9 \* 512MB = 0.108 \* 512MB。unroll memory用作数据序列化和反序列化。



###参考文献
[spark 框架](http://0x0fff.com/spark-architecture/)

[sort shuffle](https://github.com/hustnn/SparkShuffleComparison)

[tungsten secret](https://github.com/hustnn/TungstenSecret)