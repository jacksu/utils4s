package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15-12-6.
 */
object P11 {
  def encodeModified[A](xs: List[A]): List[Any] = xs match {
    case Nil => Nil
    case head :: tail => {
      if (tail.takeWhile(_ == head).isEmpty)
        head :: encodeModified(tail.dropWhile(_ == head))
      else
        (tail.takeWhile(_ == head).length + 1, head) ::
          encodeModified(tail.dropWhile(_ == head))
    }
  }

  def main(args: Array[String]) {
    println(encodeModified(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }
}
