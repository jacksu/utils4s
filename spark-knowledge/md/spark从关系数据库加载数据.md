#Spark从关系数据库加载数据

**整体思路是通过partition并行链接关系数据库。**

实现：

##1. 加载驱动程序

正确配置：

```scala
--driver-class-path "driver_local_file_system_jdbc_driver1.jar:driver_local_file_system_jdbc_driver2.jar" 
  --class "spark.executor.extraClassPath=executors_local_file_system_jdbc_driver1.jar:executors_local_file_system_jdbc_driver2.jar"
```

如果需要在NoteBook中执行任务，需要在启动前设置EXTRA_CLASSPATH，执行如下命令：

```scala
export EXTRA_CLASSPATH=path_to_the_first_jar:path_to_the_second_jar
```

##2. 并行加载

有两种方式：

1）按照指定列进行统一分区

2）通过用户自定义谓词分区

###按照指定列进行统一分区
**指定列必须是数字类型**
使用方法

```scala
sqlctx.read.jdbc(url = "<URL>", table = "<TABLE>",
  columnName = "<INTEGRAL_COLUMN_TO_PARTITION>",
  lowerBound = minValue,
  upperBound = maxValue,
  numPartitions = 20,
  connectionProperties = new java.util.Properties()
)
```

###通过用户自定义谓词分区

使用方法

```scala
val predicates = Array("2015-06-20" -> "2015-06-30", "2015-07-01" -> "2015-07-10", "2015-07-11" -> "2015-07-20",
  "2015-07-21" -> "2015-07-31").map {
    case (start, end) => s"cast(DAT_TME as date) >= date '$start' " + "AND cast(DAT_TME as date) <= date '$end'"
}
sqlctx.read.jdbc(url = "<URL>", table = "<TABLE>", predicates = predicates, connectionProperties = new java.util.Properties())
```

##3.表格union

```scala
def readTable(table: String): DataFrame
List("<TABLE1>", "<TABLE2>", "<TABLE3>").par.map(readTable).reduce(_ unionAll _)
```

 .par 表示readTable函数会并行调用，而不是线性顺序。
 
##4.映射为Case Class
 
 ```scala
 case class MyClass(a: Long, b: String, c: Int, d: String, e: String)
dataframe.map {
  case Row(a: java.math.BigDecimal, b: String, c: Int, _: String, _: java.sql.Date,
           e: java.sql.Date, _: java.sql.Timestamp, _: java.sql.Timestamp, _: java.math.BigDecimal,
           _: String) => MyClass(a = a.longValue(), b = b, c = c, d = d.toString, e = e.toString)
}
 ```
 
 不可以处理包含null值的记录。可以通过
 
 ```scala
 dataframe.na.drop()
 ```
 
 通过处理后，丢弃包含null的记录。
#参考

[利用tachyong优化任务从小时到秒](https://dzone.com/articles/Accelerate-In-Memory-Processing-with-Spark-from-Hours-to-Seconds-With-Tachyon)