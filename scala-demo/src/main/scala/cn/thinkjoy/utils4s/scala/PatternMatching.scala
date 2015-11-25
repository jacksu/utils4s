package cn.thinkjoy.utils4s.scala

/**
 * Pattern Matching
 *
 */

object PatternMatching {
  def matchTest(x: Int) = x match {
    case 1 => "One"
    case 2 => "Two"
    case _ => "Other"
  }

  def goldilocks(expr: Any) = expr match {
    case ("porridge", "Papa") => "Papa eating porridge"
    case ("porridge", _) => "Mama eating porridge"
    case ("porridge", "Baby") => "Baby eating porridge"
    case _ => "what?"
  }

  /**
   * 模式匹配代替表达式
   * @param expr
   * @return
   */
  def expression(expr: Any) = expr match {
    case ("porridge", bear) => bear + " said someone's been eating my porridge"
    case ("chair", bear) => bear + " said someone's been sitting in my chair"
    case ("bed", bear) => bear + " said someone's been sleeping in my bed"
    case _ => "what?"
  }

  def patternEquals(i: Int, j: Int) = j match {
    case `i` => true
    case _ => false
  }

  //模式匿名函数
  val transformFn:(String, Int)=>String = { case (w, _) => w }

  def main(args: Array[String]) {

    println(matchTest(3))


    val stuff = "blue"
    val myStuff = stuff match {
      case "red" => println("RED"); 1
      case "blue" => println("BLUE"); 2
      case "green" => println("GREEN"); 3
      case _ => println(stuff); 0 //case _ will trigger if all other cases fail.
    }
    assert(myStuff == 2)

    val complex = stuff match {
      case "red" => (255, 0, 0)
      case "green" => (0, 255, 0)
      case "blue" => (0, 0, 255)
      case _ => println(stuff); 0
    }
    assert(complex == (0,0,255))

    //模式匹配通配符
    assert(goldilocks(("porridge", "Mama")) == "Mama eating porridge")

    //模式匹配代替表达式
    println(expression( ("chair", "jack")))

    println(patternEquals(3,3))

  }
}
