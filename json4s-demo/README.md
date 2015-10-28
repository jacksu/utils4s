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

> * String -> AST
```scala
val ast=parse(""" {"name":"test", "numbers" : [1, 2, 3, 4] } """)
result: JObject(List((name,JString(test)), (numbers,JArray(List(JInt(1), JInt(2), JInt(3), JInt(4))))))
```
> * Json DSL -> AST
```scala
import org.json4s.JsonDSL._
//DSL implicit AST
val json2 = ("name" -> "joe") ~ ("age" -> Some(35))
println(json2)
result:JObject(List((name,JString(joe)), (age,JInt(35))))
```
> * AST -> String
```scala
val str=compact(render(json2))
println(str)
result:{"name":"joe","age":35}
//pretty
val pretty=pretty(render(json2))
println(pretty)
result:
{
  "name" : "joe",
  "age" : 35
}
```

> * AST operation
```scala
val json4 = parse( """
     { "name": "joe",
       "children": [
         {
           "name": "Mary",
           "age": 5
         },
         {
           "name": "Mazy",
           "age": 3
         }
       ]
     }
                   """)
//注意\和\\的区别
//{"name":"joe","name":"Mary","name":"Mazy"}
println(compact(render(json4 \\ "name")))
//"joe"
println(compact(render(json4 \ "name")))
//[{"name":"Mary","age":5},{"name":"Mazy","age":3}]
println(compact(render(json4 \\ "children")))
//["Mary","Mazy"]
println(compact(render(json4 \ "children" \ "name")))
//{"name":"joe"}
println(compact(render(json4 findField {
  case JField("name", _) => true
  case _ => false
})))
//{"name":"joe","name":"Mary","name":"Mazy"}
println(compact(render(json4 filterField {
  case JField("name", _) => true
  case _ => false
})))
```

> * AST -> case class
```scala
implicit val formats = DefaultFormats
val json5 = parse("""{"first_name":"Mary"}""")
case class Person(`firstName`: String)
val json6=json5 transformField {
  case ("first_name", x) => ("firstName", x)
}
println(json6.extract[Person])
println(json5.camelizeKeys.extract[Person])
result:
Person(Mary)
Person(Mary)
```

参考：
[json4s](https://github.com/json4s/json4s)