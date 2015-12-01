package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/11/30.
 */
object P02 {
  def penultimate[A](ls: List[A]): A = ls match {
    case h :: _ :: Nil => h
    case _ :: tail => penultimate(tail)
    case _ => throw new NoSuchElementException
  }

  def main(args: Array[String]) {
    println(penultimate(List(1, 1, 2, 3, 5, 8)))
    println(penultimate(List(1)))
  }

}
