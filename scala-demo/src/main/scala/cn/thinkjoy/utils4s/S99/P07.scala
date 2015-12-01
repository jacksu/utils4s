package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/1.
 */
object P07 {
  def flatten(xs:List[Any]):List[Any]=xs flatMap {
    case l:List[_]=> flatten(l)
    case e=> List(e)
  }

  def main(args: Array[String]) {
    println(flatten(List(List(1, 1), 2, List(3, List(5, 8)))))
  }
}
