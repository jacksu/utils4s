#scala-demo

[协变、逆变、上界、下界](md/协变逆变上界下界.md)

[提取器](https://windor.gitbooks.io/beginners-guide-to-scala/content/chp1-extractors.html)

## Future和Promise

[Scala Future and Promise](http://colobu.com/2015/06/11/Scala-Future-and-Promise/)

[Scala中使用Future进行并发处理](http://m.blog.csdn.net/blog/ratsniper/47177619)

##执行shell命令
```scala
val source = Source.fromURL("http://www.baidu.com","UTF-8")
println(source.mkString)
import sys.process._
"ls -la ." !
val result = "ls -l ." #| "grep README" #| "wc -l" !!
//!!必须空一行

println(result)
"grep baidu" #< new URL("http://www.baidu.com") !
```

学习scala的测试用例,参考于[scala练习](http://scala-exercises.47deg.com)