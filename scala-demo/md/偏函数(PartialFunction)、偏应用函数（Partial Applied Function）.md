#偏函数（PartialFunction）、部分应用函数（Partial Applied Function）

##偏函数（PartialFunction）

偏函数是只对函数定义域的一个子集进行定义的函数。 scala中用scala.PartialFunction[-T, +S]类来表示

scala可以通过模式匹配来定义偏函数, 下面这两种方式定义的函数, 都可以认为是偏函数, 因为他们都只对其定义域Int的部分值做了处理. 那么像p1哪有定义成PartialFunction的额外好处是, 你可以在调用前使用一个isDefinedAt方法, 来校验参数是否会得到处理.  或者在调用时使用一个orElse方法, 该方法接受另一个偏函数,用来定义当参数未被偏函数捕获时该怎么做. 也就是能够进行显示的声明. 在实际代码中最好使用PartialFunction来声明你确实是要定义一个偏函数, 而不是漏掉了什么.

```scala
def p1:PartialFunction[Int, Int] = {
    case x if x > 1 => 1
}
p1.isDefinedAt(1)

def p2 = (x:Int) => x match {
    case x if x > 1 => 1
}
```

##部分应用函数（Partial Applied Function）

是指一个函数有N个参数, 而我们为其提供少于N个参数, 那就得到了一个部分应用函数. 

比如我先定义一个函数
```scala
def sum(a:Int,b:Int,c:Int) = a + b + c
```
那么就可以从这个函数衍生出一个偏函数是这样的:
```scala
def p_sum = sum(1, _:Int, _:Int)
```
于是就可以这样调用p_sum(2,3), 相当于调用sum(1,2,3) 得到的结果是6. 这里的两个_分别对应函数sum对应位置的参数. 所以你也可以定义成
```scala
def p_sum = sum (_:Int, 1, _:Int)
```