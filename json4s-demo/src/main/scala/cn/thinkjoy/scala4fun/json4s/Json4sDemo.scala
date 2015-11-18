package cn.thinkjoy.utils4s.json4s

import org.json4s._
import org.json4s.jackson.JsonMethods._


object Json4sDemo {
  def main(args: Array[String]) {
    //=========== 通过字符串解析为json AST ==============
    val json1 = """ {"name":"test", "numbers" : [1, 2, 3, 4] } """
    println(parse(json1))

    //============= 通过DSL解析为json AST ===========
    import org.json4s.JsonDSL._
    //DSL implicit AST
    val json2 = ("name" -> "joe") ~ ("age" -> Some(35))
    println(json2)
    println(render(json2))

    case class Winner(id: Long, numbers: List[Int])
    case class Lotto(id: Long, winningNumbers: List[Int], winners: List[Winner], drawDate: Option[java.util.Date])
    val winners = List(Winner(23, List(2, 45, 34, 23, 3, 5)), Winner(54, List(52, 3, 12, 11, 18, 22)))
    val lotto = Lotto(5, List(2, 45, 34, 23, 7, 5, 3), winners, None)
    val json3 =
      ("lotto" ->
        ("lotto-id" -> lotto.id) ~
          ("winning-numbers" -> lotto.winningNumbers) ~
          ("draw-date" -> lotto.drawDate.map(_.toString)) ~
          ("winners" ->
            lotto.winners.map { w =>
              (("winner-id" -> w.id) ~
                ("numbers" -> w.numbers))
            }))
    println(render(json3))


    //=================== 转化为String =============
    //println(compact(json1))
    println(compact(json2))
    //render用默认方式格式化空字符
    println(compact(render(json2)))
    println(compact(render(json3)))

    //println(pretty(json1))
    println(pretty(render(json2)))
    println(pretty(render(json3)))


    //=========== querying json ===============
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
    // TODO name:"joe"
    val ages = for {
      JObject(child) <- json4
      JField("age", JInt(age)) <- child
      if age > 4
    } yield age
    val name = for{
      JString(name) <- json4
    } yield name
    println(ages)
    //List(joe, Mary, Mazy)
    println(name)
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

    //============== extract value =================
    implicit val formats = DefaultFormats
    val json5 = parse("""{"first_name":"Mary"}""")
    case class Person(`firstName`: String)
    val json6=json5 transformField {
      case ("first_name", x) => ("firstName", x)
    }
    println(json6.extract[Person])
    println(json5.camelizeKeys.extract[Person])

    //================ xml 2 json ===================
    import org.json4s.Xml.{toJson, toXml}
    val xml =
      <users>
        <user>
          <id>1</id>
          <name>Harry</name>
        </user>
        <user>
          <id>2</id>
          <name>David</name>
        </user>
      </users>

    val json = toJson(xml)
    println(pretty(render(json)))
    println(pretty(render(json transformField {
      case ("id", JString(s)) => ("id", JInt(s.toInt))
      case ("user", x: JObject) => ("user", JArray(x :: Nil))
    })))
    //================ json 2 xml ===================
    println(toXml(json))
  }
}
