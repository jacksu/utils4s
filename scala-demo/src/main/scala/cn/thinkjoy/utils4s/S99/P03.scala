package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/1.
 */
object P03 {
  def nth[A](n: Int, xs: List[A]): A = {
    if (xs.size <= n)
      throw new NoSuchElementException
    else
      xs(n)
  }

  def main(args: Array[String]) {
    println(nth(2, List(1, 1, 2, 3, 5, 8)))
    println(nth(6, List(1, 1, 2, 3, 5, 8)))
  }
}
