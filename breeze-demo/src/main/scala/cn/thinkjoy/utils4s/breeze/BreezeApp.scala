package cn.thinkjoy.utils4s.breeze

//包含线性代数包（linear algebra）
import breeze.linalg._

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
    println(x(1))

    //==========DenseMatrix===========
    /**
     * 0  0  0  0  0
     * 0  0  0  0  0
     * 0  0  0  0  0
     * 0  0  0  0  0
     * 0  0  0  0  0
     */
    val m=DenseMatrix.zeros[Int](5,5)
    println(m)

    /**
     * 向量是列式的
     * 0  0  0  0  1
     * 0  0  0  0  2
     * 0  0  0  0  3
     * 0  0  0  0  4
     * 0  0  0  0  5
     */
    m(::,4):=DenseVector(1,2,3,4,5)
    println(m)
    //5
    println(max(m))
    //15
    println(sum(m))
    //DenseVector(1.0, 1.5, 2.0)
    println(linspace(1,2,3))

    //==========对角线============
    /**
     * 1.0  0.0  0.0
     * 0.0  1.0  0.0
     * 0.0  0.0  1.0
     */
    println(DenseMatrix.eye[Double](3))
    /**
     * 1.0  0.0  0.0
     * 0.0  2.0  0.0
     * 0.0  0.0  3.0
     */
    println(diag(DenseVector(1.0,2.0,3.0)))
  }
}
