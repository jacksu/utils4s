package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/1.
 */
object P04 {
  def length[A](xs: List[A]): Int = xs match {
    case Nil => 0
    case _ :: tail => 1 + length(tail)
  }

  def main(args: Array[String]) {
    println(length(List(1, 1, 2, 3, 5, 8)))
  }
}
