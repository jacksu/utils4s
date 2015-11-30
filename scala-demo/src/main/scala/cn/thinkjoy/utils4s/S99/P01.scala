package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/11/30.
 */
object P01 {
  def last[A](ls: List[A]): A = ls.last

  def main(args: Array[String]) {
    println(last(List(1, 1, 2, 3, 5, 8)))
  }
}
