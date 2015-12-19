#nscala-time

有时间的操作，文档不全，不知道每月的最大一天是什么，暂时还不知道如何使用scala for操作日期段，如下使用。

```scala
//不可以这样
for(current<-DateTime.parse("2014-07-7") to DateTime.parse("2014-07-8")){
    println(current)
}
```


谢谢jjcipher，补全demo