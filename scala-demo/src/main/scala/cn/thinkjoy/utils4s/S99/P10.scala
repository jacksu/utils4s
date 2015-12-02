package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/2.
 */
object P10 {
  def encode[A](xs: List[A]): List[Any] = xs match {
    case Nil => Nil
    case head :: tail => (tail.takeWhile(_ == head).length+1, head) :: encode(tail.dropWhile(_ == head))
  }

  def main(args: Array[String]) {
    println(encode(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }
}
