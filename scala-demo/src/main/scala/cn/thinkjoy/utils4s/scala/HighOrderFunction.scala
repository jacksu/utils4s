package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15/11/29.
 *
 *
 */

case class Email(subject: String,
                 text: String,
                 sender: String,
                 recipient: String)

object Email {
  type EmailFilter = Email => Boolean

  def newMailsForUser(mails: Seq[Email], f: EmailFilter) = mails.filter(f)

  object EmailFilterFactory {
    //谓词函数
    def complement[A](predicate: A => Boolean) = (a: A) => !predicate(a)

    val sentByOneOf: Set[String] => EmailFilter =
      senders => email => senders.contains(email.sender)
    //val notSentByAnyOf: Set[String] => EmailFilter =
    //  senders => email => !senders.contains(email.sender)
    //函数组合
    val notSentByAnyOf = sentByOneOf andThen (complement(_))
    //运行是有错误的
    //val notSentByAnyOf = (complement(_)) compose (sentByOneOf)
    type SizeChecker = Int => Boolean
    val sizeConstraint: SizeChecker => EmailFilter =
      f => email => f(email.text.size)
    val minimumSize: Int => EmailFilter =
      n => sizeConstraint(_ >= n)
    val maximumSize: Int => EmailFilter =
      n => sizeConstraint(_ <= n)
  }

}

object HighOrderFunction {

  def main(args: Array[String]) {
    val emailFilter: Email.EmailFilter = Email.EmailFilterFactory.notSentByAnyOf(Set("johndoe@example.com"))
    val mails = Email(
      subject = "It's me again, your stalker friend!",
      text = "Hello my friend! How are you?",
      sender = "johndoe@example.com",
      recipient = "me@example.com") :: Nil
    Email.newMailsForUser(mails, emailFilter) // returns an empty list

  }
}
