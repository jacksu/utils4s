> RDD、DataFrame和DataSet是容易产生混淆的概念，必须对其相互之间对比，才可以知道其中异同。

##RDD和DataFrame

![RDD-DataFrame](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/rdd-dataframe-dataset/rdd-dataframe.png)

上图直观地体现了DataFrame和RDD的区别。左侧的RDD[Person]虽然以Person为类型参数，但Spark框架本身不了解Person类的内部结构。而右侧的DataFrame却提供了详细的结构信息，使得Spark SQL可以清楚地知道该数据集中包含哪些列，每列的名称和类型各是什么。DataFrame多了数据的结构信息，即schema。RDD是分布式的Java对象的集合。DataFrame是分布式的Row对象的集合。DataFrame除了提供了比RDD更丰富的算子以外，更重要的特点是提升执行效率、减少数据读取以及执行计划的优化，比如filter下推、裁剪等。

###提升执行效率 

RDD API是函数式的，强调不变性，在大部分场景下倾向于创建新对象而不是修改老对象。这一特点虽然带来了干净整洁的API，却也使得Spark应用程序在运行期倾向于创建大量临时对象，对GC造成压力。在现有RDD API的基础之上，我们固然可以利用mapPartitions方法来重载RDD单个分片内的数据创建方式，用复用可变对象的方式来减小对象分配和GC的开销，但这牺牲了代码的可读性，而且要求开发者对Spark运行时机制有一定的了解，门槛较高。另一方面，Spark SQL在框架内部已经在各种可能的情况下尽量重用对象，这样做虽然在内部会打破了不变性，但在将数据返回给用户时，还会重新转为不可变数据。利用 DataFrame API进行开发，可以免费地享受到这些优化效果。 

###减少数据读取

分析大数据，最快的方法就是 ——忽略它。这里的“忽略”并不是熟视无睹，而是根据查询条件进行恰当的剪枝。

上文讨论分区表时提到的分区剪 枝便是其中一种——当查询的过滤条件中涉及到分区列时，我们可以根据查询条件剪掉肯定不包含目标数据的分区目录，从而减少IO。 

对于一些“智能”数据格 式，Spark SQL还可以根据数据文件中附带的统计信息来进行剪枝。简单来说，在这类数据格式中，数据是分段保存的，每段数据都带有最大值、最小值、null值数量等 一些基本的统计信息。当统计信息表名某一数据段肯定不包括符合查询条件的目标数据时，该数据段就可以直接跳过（例如某整数列a某段的最大值为100，而查询条件要求a > 200）。 

此外，Spark SQL也可以充分利用RCFile、ORC、Parquet等列式存储格式的优势，仅扫描查询真正涉及的列，忽略其余列的数据。

###执行优化

![人口数据分析示例](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/rdd-dataframe-dataset/filter-down.png)

为了说明查询优化，我们来看上图展示的人口数据分析的示例。图中构造了两个DataFrame，将它们join之后又做了一次filter操作。如果原封不动地执行这个执行计划，最终的执行效率是不高的。因为join是一个代价较大的操作，也可能会产生一个较大的数据集。如果我们能将filter下推到 join下方，先对DataFrame进行过滤，再join过滤后的较小的结果集，便可以有效缩短执行时间。而Spark SQL的查询优化器正是这样做的。简而言之，逻辑查询计划优化就是一个利用基于关系代数的等价变换，将高成本的操作替换为低成本操作的过程。 

得到的优化执行计划在转换成物 理执行计划的过程中，还可以根据具体的数据源的特性将过滤条件下推至数据源内。最右侧的物理执行计划中Filter之所以消失不见，就是因为溶入了用于执行最终的读取操作的表扫描节点内。 

对于普通开发者而言，查询优化 器的意义在于，即便是经验并不丰富的程序员写出的次优的查询，也可以被尽量转换为高效的形式予以执行。 

##RDD和DataSet

* > DataSet以Catalyst逻辑执行计划表示，并且数据以编码的二进制形式被存储，不需要反序列化就可以执行sorting、shuffle等操作。

* > DataSet创立需要一个显式的Encoder，把对象序列化为二进制，可以把对象的scheme映射为Spark
 SQl类型，然而RDD依赖于运行时反射机制。

通过上面两点，DataSet的性能比RDD的要好很多，可以参见[3]

##DataFrame和DataSet

Dataset可以认为是DataFrame的一个特例，主要区别是Dataset每一个record存储的是一个强类型值而不是一个Row。因此具有如下三个特点：

* > DataSet可以在编译时检查类型

* > 并且是面向对象的编程接口。用wordcount举例：

```scala
//DataFrame

// Load a text file and interpret each line as a java.lang.String
val ds = sqlContext.read.text("/home/spark/1.6/lines").as[String]
val result = ds
  .flatMap(_.split(" "))               // Split on whitespace
  .filter(_ != "")                     // Filter empty words
  .toDF()                              // Convert to DataFrame to perform aggregation / sorting
  .groupBy($"value")                   // Count number of occurences of each word
  .agg(count("*") as "numOccurances")
  .orderBy($"numOccurances" desc)      // Show most common words first
```

```scala
//DataSet,完全使用scala编程，不要切换到DataFrame

val wordCount = 
  ds.flatMap(_.split(" "))
    .filter(_ != "")
    .groupBy(_.toLowerCase()) // Instead of grouping on a column expression (i.e. $"value") we pass a lambda function
    .count()
```

* > 后面版本DataFrame会继承DataSet，DataFrame是面向Spark SQL的接口。

DataFrame和DataSet可以相互转化，`df.as[ElementType]`这样可以把DataFrame转化为DataSet，`ds.toDF()`这样可以把DataSet转化为DataFrame。

##参考
[1] [Spark SQL结构化分析](http://www.iteye.com/news/30658)

[2] [解读2015之Spark篇：新生态系统的形成](http://www.infoq.com/cn/articles/2015-Review-Spark)

[3] [Introducing Spark Datasets](https://databricks.com/blog/2016/01/04/introducing-spark-datasets.html)

[4] [databricks example](https://docs.cloud.databricks.com/docs/spark/1.6/index.html#examples/Dataset%20Wordcount.html)