正如你所知，spark实现了多种shuffle方法，通过 spark.shuffle.manager来确定。暂时总共有三种：hash shuffle、sort shuffle和tungsten-sort shuffle，从1.2.0开始默认为sort shuffle。本节主要介绍sort shuffle。

从1.2.0开始默认为sort shuffle(**spark.shuffle.manager** = sort)，实现逻辑类似于Hadoop MapReduce，Hash Shuffle每一个reducers产生一个文件，但是Sort Shuffle只是产生一个按照reducer id排序可索引的文件，这样，只需获取有关文件中的相关数据块的位置信息，并fseek就可以读取指定reducer的数据。但对于rueducer数比较少的情况，Hash Shuffle明显要比Sort Shuffle快，因此Sort Shuffle有个“fallback”计划，对于reducers数少于 “spark.shuffle.sort.bypassMergeThreshold” (200 by default)，我们使用fallback计划，hashing相关数据到分开的文件，然后合并这些文件为一个，具体实现为[BypassMergeSortShuffleWriter](https://github.com/apache/spark/blob/master/core/src/main/java/org/apache/spark/shuffle/sort/BypassMergeSortShuffleWriter.java)。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark_sort_shuffle.png)

