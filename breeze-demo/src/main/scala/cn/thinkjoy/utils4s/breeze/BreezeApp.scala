package cn.thinkjoy.utils4s.breeze

//包含线性代数包（linear algebra）
import breeze.linalg.{SparseVector, DenseVector}

/**
 * jacksu
 *
 */
object BreezeApp {
  def main(args: Array[String]) {
    //=========两种矢量的区别，dense分配内存，sparse不分配=========
    //DenseVector(0.0, 0.0, 0.0, 0.0, 0.0)
    //底层是Array
    val x = DenseVector.zeros[Double](5)
    println(x)

    //SparseVector
    val y = SparseVector.zeros[Double](5)
    println(y)

    //===========操作对应的值===============
    //DenseVector(0.0, 2.0, 0.0, 0.0, 0.0)
    x(1)=2
    println(x)

    //SparseVector((1,2.0))
    y(1)=2
    println(y)

    //===========slice==========
    //DenseVector(0.5, 0.5)
    println(x(3 to 4):=.5)
    //DenseVector(0.0, 2.0, 0.0, 0.5, 0.5)
    println(x)

  }
}
