package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/2.
 */
object P09 {
  def pack[A](xs: List[A]): List[Any] = xs match {
    case Nil => Nil
    case head :: tail => (head::tail.takeWhile(head == _)) :: pack(tail.dropWhile(_ == head))
  }

  def main(args: Array[String]) {
    println(pack(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }
}
