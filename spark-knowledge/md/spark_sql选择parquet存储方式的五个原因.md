#spark SQL选择parquet存储方式的五个原因

> 1 采用parquet格式，spark SQL有10x的性能提升

> 2 Spark SQL会工作比较好，因为读取数据量变小

> 3 减少IO，会filter下推

> 4 1.6.0中更高的扫描吞吐量，CPU使用较低，磁盘吞吐量比较高

> 5 Efficient Spark execution graph



##参考

[https://developer.ibm.com/hadoop/blog/2016/01/14/5-reasons-to-choose-parquet-for-spark-sql/](https://developer.ibm.com/hadoop/blog/2016/01/14/5-reasons-to-choose-parquet-for-spark-sql/)