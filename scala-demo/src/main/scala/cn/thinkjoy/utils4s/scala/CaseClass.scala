package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15/10/24.
 */

abstract class Term
case class  Var(name: String) extends Term
case class Fun(arg: String, body: Term) extends Term
case class App(f: Term, v: Term) extends Term
case class Dog(name: String, breed: String) // Doberman

object CaseClass {
  def main(args: Array[String]) {
    def printTerm(term: Term) {
      term match {
        case Var(n) =>
          print(n)
        case Fun(x, b) =>
          print("^" + x + ".")
          printTerm(b)
        case App(f, v) =>
          Console.print("(")
          printTerm(f)
          print(" ")
          printTerm(v)
          print(")")
      }
    }
    def isIdentityFun(term: Term): Boolean = term match {
      case Fun(x, Var(y)) if x == y => true
      case _ => false
    }
    val id = Fun("x", Var("x"))
    val t = Fun("x", Fun("y", App(Var("x"), Var("y"))))
    printTerm(t)
    println
    println(isIdentityFun(id))
    println(isIdentityFun(t))

    val d1 = Dog("Scooby", "Doberman")

    val d2 = d1.copy(name = "Scooby Doo") // copy the case class but change the name in the copy
    println(d2.name)

    val d3=Dog.unapply(d2).get
    println(d3._1)
  }
}
