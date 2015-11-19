package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15/11/19.
 */
object CovariantAndContravariant {
  def main(args: Array[String]) {

    class Animal {}
    class Bird extends Animal {}
    //协变
    class Covariant[+T](t:T){}
    val cov = new Covariant[Bird](new Bird)
    val cov2:Covariant[Animal] = cov
    //逆变
    class Contravariant[-T](t: T) {
    }
    val c: Contravariant[Animal] = new Contravariant[Animal](new Animal)
    val c2: Contravariant[Bird] = c

  }
}
