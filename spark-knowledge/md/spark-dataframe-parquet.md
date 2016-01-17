Apache Parquet作为文件格式最近获得了显著关注，假设你有一个100列的表，大部分时间你只需要访问3-10列，行存储，不管你需要不需要它们，你必须扫描所有。Apache Parquet是列存储，如果需要3列，那么只有这3列被load。并且datatype、compression和quality非常好。

下面我们来介绍如何把一个表存储为Parquet和如何加载。

首先建立一个表格：
 
| *first_name*	| *last_name* | gender |
| ------------- |:-------------:| :-----:|
|Barack | Obama |	M |
|Bill |	Clinton |	M |
|Hillary |	Clinton |	F |

 
Spark SQL:

```scala
val hc = new org.apache.spark.sql.hive.HiveContext(sc)
import hc.implicits._
case class Person(firstName: String, lastName: String, gender: String)
val personRDD = sc.textFile("person").map(_.split("\t")).map(p => Person(p(0),p(1),p(2)))
val person = personRDD.toDF
person.registerTempTable("person")
val males = hc.sql("select * from person where gender='M'")
males.collect.foreach(println)
```
保存DF为Parquet格式:

```scala
person.write.parquet("person.parquet")
```

Hive中建立Parquet格式的表:

```hive
create table person_parquet like person stored as parquet;
insert overwrite table person_parquet select * from person;
```

加载Parquet文件不再需要case class。

```scala
val personDF = hc.read.parquet("person.parquet")
personDF.registerAsTempTable("pp")
val males = hc.sql("select * from pp where gender='M'")
males.collect.foreach(println)
```
parquet文件的性能经过简单的group by操作测试，性能可以提高一倍多。

Sometimes Parquet files pulled from other sources like Impala save String as binary. To fix that issue, add the following line right after creating SqlContext:

```scala
sqlContext.setConf("spark.sql.parquet.binaryAsString","true")
```

##参考

[http://www.infoobjects.com/spark-cookbook/](http://www.infoobjects.com/spark-cookbook/)