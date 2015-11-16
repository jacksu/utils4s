#单位换算以及不同单位进行运算

##转换

```scala
(Hours(2) + Days(1) + Seconds(1)).toSeconds //93601.0
```
##toString

```scala
Days(1) toString time.Seconds //86400.0 s
```

##toTuple

```scala
Days(1) toTuple time.Seconds //(86400.0,s)
```

**测试不支持map**

##精度判断

```scala
implicit val tolerance = Watts(.1)      // implicit Power: 0.1 W 
val load = Kilowatts(2.0)               // Power: 2.0 kW
val reading = Kilowatts(1.9999)         // Power: 1.9999 kW

 // uses implicit tolerance
load =~ reading // true
```
[squants](https://github.com/garyKeorkunian/squants)