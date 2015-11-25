package cn.thinkjoy.utils4s.scala

/**
 *
 * 提取器测试用例，模式匹配可以解构各种数据结构，包括 列表 、
 * 流 ，以及 样例类,归功于提取器。构造器从给定的参数列表创
 * 建一个对象， 而提取器却是从传递给它的对象中提取出构造该对象的参数
 * Created by jacksu on 15/11/24.
 */

object ExtractorApp {

  case class User(firstName: String, lastName: String, score: Int)

  trait User1 {
    def name: String

    def score: Int
  }

  class FreeUser(
                  val name: String,
                  val score: Int,
                  val upgradeProbability: Double
                  ) extends User1

  class PremiumUser(
                     val name: String,
                     val score: Int
                     ) extends User1

  object FreeUser {
    def unapply(user: FreeUser): Option[(String, Int, Double)] =
      Some((user.name, user.score, user.upgradeProbability))
  }

  object PremiumUser {
    def unapply(user: PremiumUser): Option[(String, Int)] =
      Some((user.name, user.score))
  }

  def main(args: Array[String]) {
    val user1 = User("jack", "su", 98)
    val user2 = User("jack", "su", 90)
    val xs = List(user1, user2)
    println(advance(xs))

    //多值提取
    val user: User1 = new FreeUser("Daniel", 3000, 0.7d)
    val str = user match {
      case FreeUser(name, _, p) =>
        if (p > 0.75) s"$name, what can we do for you today?"
        else s"Hello $name"
      case PremiumUser(name, _) =>
        s"Welcome back, dear $name"
    }
    println(str)

    //TODO 遇到bool提取添加
  }

  def advance(xs: List[User]) = xs match {
    case User(_, _, score1) :: User(_, _, score2) :: _ => score1 - score2
    case _ => 0
  }
}
