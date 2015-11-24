package cn.thinkjoy.utils4s.scala

/**
 *
 * 提取器测试用例
 *
 * Created by jacksu on 15/11/24.
 */

object ExtractorApp {

  case class User(firstName: String, lastName: String, score: Int)

  def main(args: Array[String]) {
    val user1 = User("jack", "su", 98)
    val user2 = User("jack", "su", 90)
    val xs = List(user1, user2)
    println(advance(xs))
  }

  def advance(xs: List[User]) = xs match {
    case User(_, _, score1) :: User(_, _, score2) :: _ => score1 - score2
    case _ => 0
  }
}
