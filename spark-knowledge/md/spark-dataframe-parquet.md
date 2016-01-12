Apache Parquet as a file format has garnered significant attention recently. Let’s say you have a table with 100 columns, most of the time you are going to access 3-10 columns. In Row oriented format all columns are scanned whether you need them or not.
Apache Parquet saves data in column oriented fashion, so if you need 3 columns, only data of those 3 columns get loaded. Another benefit is that since all data in a given column is the same datatype (obviously), compression quality is far superior.
In this recipe we’ll learn how to save a table in Parquet format and then how to load it back.
Let’s use the person table we created in the other recipe.
 
first_name	last_name	gender
Barack	Obama	M
Bill	Clinton	M
Hillary	Clinton	F
 
Let’s load it in Spark SQL:

```scala
val hc = new org.apache.spark.sql.hive.HiveContext(sc)
import hc.implicits._
case class Person(firstName: String, lastName: String, gender: String)
val personRDD = sc.textFile("person").map(_.split("\t")).map(p =&gt; Person(p(0),p(1),p(2)))
val person = personRDD.toDF
person.registerTempTable("person")
val males = hc.sql("select * from person where gender='M'")
males.collect.foreach(println)
```

Now let’s save this person SchemaRDD to Parquet format:

```scala
person.saveAsParquetFile("person.parquet")
```

There is an alternative way to save to Parquet if you have data already in the Hive table:

```hive
create table person_parquet like person stored as parquet;
insert overwrite table person_parquet select * from person;
```
Now let’s load this Parquet file. There is no need of using a case class anymore as schema is preserved in Parquet.

```scala
val personDF = hc.load("person.parquet")
 personDF.registerAsTempTable("pp")
val males = hc.sql("select * from pp where gender='M'")
males.collect.foreach(println)
```

Sometimes Parquet files pulled from other sources like Impala save String as binary. To fix that issue, add the following line right after creating SqlContext:

```scala
sqlContext.setConf("spark.sql.parquet.binaryAsString","true")
```

##参考

[http://www.infoobjects.com/spark-cookbook/](http://www.infoobjects.com/spark-cookbook/)