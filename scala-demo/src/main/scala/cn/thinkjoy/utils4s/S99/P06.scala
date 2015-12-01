package cn.thinkjoy.utils4s.S99

/**
 * Created by jacksu on 15/12/1.
 */
object P06 {
  def isPalindrome[A](xs:List[A]):Boolean={
    xs.reverse == xs
  }

  def main(args: Array[String]) {
    println(isPalindrome(List(1, 2, 3, 2, 1)))
    println(isPalindrome(List(2, 3, 2, 1)))
  }
}
