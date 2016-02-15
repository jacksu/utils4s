package cn.thinkjoy.utils4s.spark.dataframe

import java.sql.{ Date, Timestamp }
import java.util.Calendar

import cn.thinkjoy.utils4s.spark.dataframe.SparkDataFrameApp._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{ MutableAggregationBuffer, UserDefinedAggregateFunction }
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

/**
 * http://zhangyi.farbox.com/post/kai-yuan-kuang-jia/udf-and-udaf-in-spark
 * Created by xbsu on 16/1/18.
 */
object SparkDataFrameUDFApp extends SparkSQLSupport("UDFApp") {
  def main(args: Array[String]) {

    val path = "spark-dataframe-demo/src/main/resources/b.txt"
    val df = createTableFromStr(path, "people", "age name", f)
    //使用udf 1.5.2只能使用sqlContext
    //TODO 查找sqlCOntext和hiveContext差别

    /**
     * UDF
     */

    //更详细解释：http://zhangyi.farbox.com/post/kai-yuan-kuang-jia/udf-and-udaf-in-spark
    sqlContext.udf.register("getSourceType", getSourceType(_: String))
    sqlContext.sql("SELECT base64(age),getSourceType(name) FROM people").show()
    //注册udf函数
    sqlContext.udf.register("longLength", lengthLongerThan _)

    sqlContext.sql("select * from people where longLength(name,10)").show()

    //若使用DataFrame的API，则可以以字符串的形式将UDF传入
    df.filter("longLength(name,10)").show()

    //DataFrame的API也可以接收Column对象，
    //可以用$符号来包裹一个字符串表示一个Column。
    //$是定义在SQLContext对象implicits中的一个隐式转换。
    //此时，UDF的定义也不相同，不能直接定义Scala函数，
    //而是要用定义在org.apache.spark.sql.functions中的udf方法来接收一个函数。
    //这种方式无需register
    import org.apache.spark.sql.functions._
    val longLength = udf((bookTitle: String, length: Int) ⇒ bookTitle.length > length)
    import sqlContext.implicits._
    //用$符号来包裹一个字符串表示一个Column
    df.filter(longLength($"name", lit(10))).show()

    /**
     * UDAF(User Defined Aggregate Function)
     * 例子：当我要对销量执行年度同比计算，就需要对当年和上一年的销量分别求和，
     * 然后再利用同比公式进行计算
     */

    val sales = Seq(
      (1, "Widget Co", 1000.00, 0.00, "AZ", "2014-01-01"),
      (2, "Acme Widgets", 2000.00, 500.00, "CA", "2014-02-01"),
      (3, "Widgetry", 1000.00, 200.00, "CA", "2015-01-11"),
      (4, "Widgets R Us", 2000.00, 0.0, "CA", "2015-02-19"),
      (5, "Ye Olde Widgete", 3000.00, 0.0, "MA", "2015-02-28"))

    val salesRows = sc.parallelize(sales, 4)
    val salesDF = salesRows.toDF("id", "name", "sales", "discount", "state", "saleDate")
    salesDF.registerTempTable("sales")
    val current = DateRange(Timestamp.valueOf("2015-01-01 00:00:00"), Timestamp.valueOf("2015-12-31 00:00:00"))
    val yearOnYear = new YearOnYearUDAF(current)

    sqlContext.udf.register("yearOnYear", yearOnYear)
    val dataFrame = sqlContext.sql("select yearOnYear(sales, saleDate) as yearOnYear from sales")
    dataFrame.show()
  }

  def lengthLongerThan(name: String, length: Int): Boolean = {
    name.length > length
  }

  /**
   * UDF验证
   * @param remark
   * @return
   */
  def getSourceType(remark: String): Int = {
    val typePattern = "yzt_web|iphone|IPHONE|ANDROID".r
    val logType = typePattern.findFirstIn(remark).getOrElse("")

    logType match {
      case "yzt_web" ⇒ 0
      case "ANDROID" ⇒ 1
      case "IPHONE" ⇒ 2
      case "iphone" ⇒ 2
      case _ ⇒ 404
    }
  }

  /**
   * 对输入的内容转化为Row
   * @param line
   * @return
   */
  def f(line: RDD[String]): RDD[Row] = {
    line.map(_.split(" ")).map(array ⇒ Row(array(0), array(1)))
  }
}

case class DateRange(startDate: Timestamp, endDate: Timestamp) {
  def in(targetDate: Date): Boolean = {
    targetDate.before(endDate) && targetDate.after(startDate)
  }
}

class YearOnYearUDAF(current: DateRange) extends UserDefinedAggregateFunction {
  //处理的列
  override def inputSchema: StructType = {
    StructType(StructField("metric", DoubleType) :: StructField("time", DateType) :: Nil)
  }

  //保存处理的中间结果
  override def bufferSchema: StructType = {
    StructType(StructField("sumOfCurrent", DoubleType) :: StructField("sumOfPrevious", DoubleType) :: Nil)
  }

  //update函数的第二个参数input: Row对应的并非DataFrame的行，而是被inputSchema投影了的行。
  //以本例而言，每一个input就应该只有两个Field的值。倘若我们在调用这个UDAF函数时，
  //分别传入了销量和销售日期两个列的话，则input(0)代表的就是销量，input(1)代表的就是销售日期。
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if (current.in(input.getAs[Date](1))) {
      buffer(0) = buffer.getAs[Double](0) + input.getAs[Double](0)
    }
    val previous = DateRange(subtractOneYear(current.startDate), subtractOneYear(current.endDate))
    if (previous.in(input.getAs[Date](1))) {
      buffer(1) = buffer.getAs[Double](1) + input.getAs[Double](0)
    }
  }

  private def subtractOneYear(targetDate: Timestamp): Timestamp = {
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(targetDate.getTime)
    calendar.add(Calendar.YEAR, -1)

    val time = new Timestamp(calendar.getTimeInMillis)
    println(time.toString)
    time
  }

  //merge函数负责合并两个聚合运算的buffer，再将其存储到MutableAggregationBuffer中
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Double](0) + buffer2.getAs[Double](0)
    buffer1(1) = buffer1.getAs[Double](1) + buffer2.getAs[Double](1)
  }

  //initialize就是对聚合运算中间结果的初始化，在我们这个例子中，两个求和的中间值都被初始化为0d：
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer.update(0, 0d)
    buffer.update(1, 0d)
  }

  //deterministic是一个布尔值，用以标记针对给定的一组输入，UDAF是否总是生成相同的结果
  override def deterministic: Boolean = {
    true
  }

  //最终计算结果
  override def evaluate(buffer: Row): Any = {
    if (buffer.getDouble(1) == 0.0)
      0.0
    else
      (buffer.getDouble(0) - buffer.getDouble(1)) / buffer.getDouble(1) * 100
  }

  //最终返回的类型
  override def dataType: DataType = DoubleType
}