
时间数据展示有三种方式

Observations DataFrame

![Observations](http://blog.cloudera.com/wp-content/uploads/2015/12/sparkts-t1.png)

Instants DataFrame

![Instants DataFrame](http://blog.cloudera.com/wp-content/uploads/2015/12/sparkts-t2.png)

TimeSeriesRDD

![TimeSeriesRDD](http://blog.cloudera.com/wp-content/uploads/2015/12/sparkts-t3.png)

以股票数据为例，数据以tab分割，分别为年、月、日、股票代码、数量、价格

```scala
2015    8       14      ADP     194911  82.99
2015    9       14      NKE     224435  111.78
2015    9       18      DO      678664  20.18
2015    8       7       TGT     147406  78.96
```

##参考
[spark-ts](http://blog.cloudera.com/blog/2015/12/spark-ts-a-new-library-for-analyzing-time-series-data-with-apache-spark/)