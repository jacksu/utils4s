package cn.thinkjoy.utils4s.scala

/**
 * Created by jacksu on 15-12-7.
 */

object MapApp {
  case class A(name:String,oper:Long,cType:Long,count:Long)
  val enumType=List(1,2)

  def decode(t:Long): List[Long] ={
    for(x<-enumType if((t&x) != 0)) yield x.toLong
  }

  def main(args: Array[String]) {
    val list=List(A("1",2,1,1),A("1",1,1,0),A("1",2,2,0),A("1",2,3,4))
    list.flatMap {
      case A(a, b, cType,c) => for (x <- decode(cType)) yield ((a,b,x),c)
    }.groupBy(_._1).mapValues(_.map(_._2).sum).map{
      case ((a,b,c),d) => A(a,b,c,d)
    }.foreach(println)
  }
}
