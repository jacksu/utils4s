package cn.thinkjoy.utils4s

/**
 * Created by jacksu on 15/10/25.
 */
object Extract {
  def main(args: Array[String]) {
    val list = 1 :: 2 :: 3 :: 4 :: Nil
    val extract = list match {
      case List(first, second, _*) => first + second
      case _ => 0
    }
    println(extract)
  }
}
