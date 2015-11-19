package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15/11/19.
 */
object CovariantAndContravariant {
  def main(args: Array[String]) {

    class Animal {println("Animal")}
    class Bird extends Animal {println("Bird")}
    //协变
    println("========协变==========")
    class Covariant[+T](t:T){}
    val cov = new Covariant[Bird](new Bird)
    val cov2:Covariant[Animal] = cov
    //逆变
    println("=========逆变==========")
    class Contravariant[-T](t: T) {
    }
    val c: Contravariant[Animal] = new Contravariant[Animal](new Animal)
    val c2: Contravariant[Bird] = c
    //上界
    println("===========上界=============")
    class UpperBoundAnimal{println("UpperBoundAnimal")}
    class UpperBoundBird extends UpperBoundAnimal{println("UpperBoundBird")}
    class UpperBoundBlueBird extends UpperBoundBird{println("UpperBoundBlueBird")}
    class UpperBound[-T](t:T){
      def use[S <: T](s:S){println("use")}
    }
    val upper=new UpperBound[UpperBoundAnimal](new UpperBoundAnimal)
    val upper2:UpperBound[UpperBoundBird]=upper
    upper2.use(new UpperBoundBird)
    upper.use(new UpperBoundBird)
    //upper2.use(new UpperBoundAnimal) //error
    upper.use(new UpperBoundAnimal)
    upper2.use(new UpperBoundBlueBird)
    upper.use(new UpperBoundBlueBird)

    //下界
    println("=========下界=============")
    class LowerBoundAnimal(){println("LowerBoundAnimal")}
    class LowerBoundBird extends LowerBoundAnimal(){println("LowerBoundBird")}
    class LowerBoundBlueBird extends LowerBoundBird(){println("LowerBoundBlueBird")}
    class LowerBound[+T](t:T){
      def use[S >: T](s:S){println("use")}
    }
    val lower=new LowerBound[LowerBoundBlueBird](new LowerBoundBlueBird)
    val lower2:LowerBound[LowerBoundBird] = lower
    lower2.use(new LowerBoundAnimal)
    lower2.use(new LowerBoundBird)
    //TODO 确定为什么下面这个是正确的
    lower2.use(new LowerBoundBlueBird)
  }
}
