#json4s
json的各种形式的相互转化图如下：
![Json AST](https://raw.github.com/json4s/json4s/3.4/core/json.png)

其中的关键是AST，AST有如下的语法树：
```scala
sealed abstract class JValue
case object JNothing extends JValue // 'zero' for JValue
case object JNull extends JValue
case class JString(s: String) extends JValue
case class JDouble(num: Double) extends JValue
case class JDecimal(num: BigDecimal) extends JValue
case class JInt(num: BigInt) extends JValue
case class JBool(value: Boolean) extends JValue
case class JObject(obj: List[JField]) extends JValue
case class JArray(arr: List[JValue]) extends JValue

type JField = (String, JValue)
```