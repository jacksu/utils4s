package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/1.
 */
object P05 {
  def reverse[A](xs:List[A]):List[A]= xs match{
    case head::Nil => List(head)
    case head::tail => reverse(tail):::List(head)
  }

  def main(args: Array[String]) {
    println(reverse(List(1, 1, 2, 3, 5, 8)))
  }
}
