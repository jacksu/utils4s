package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/2.
 */
object P08 {
  /**   result is vector
  def compress[A](xs: List[A]) = {
    for (i <- 0 until xs.length; j = i + 1
         if (j < xs.length && xs(i) != xs(j)|| j==xs.length)
    ) yield (xs(i))
  }
    **/

  def compress[A](xs:List[A]):List[A] = xs match{
    case Nil => Nil
    case head::tail => head::compress(tail.dropWhile(_ == head))
  }

  def main(args: Array[String]) {
    println(compress(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }
}
