##单位换算以及不同单位进行运算

*已经两年没有更新*

###转换

```scala
(Hours(2) + Days(1) + Seconds(1)).toSeconds //93601.0
```
###toString

```scala
Days(1) toString time.Seconds //86400.0 s
```

###toTuple

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

###向量
```scala
    val vector: QuantityVector[Length] = QuantityVector(Kilometers(1.2), Kilometers(4.3), Kilometers(2.3))
    val magnitude: Length = vector.magnitude        // returns the scalar value of the vector
    println(magnitude)
    val normalized = vector.normalize(Kilometers)   // returns a corresponding vector scaled to 1 of the given unit
    println(normalized)

    val vector2: QuantityVector[Length] = QuantityVector(Kilometers(1.2), Kilometers(4.3), Kilometers(2.3))
    val vectorSum = vector + vector2        // returns the sum of two vectors
    println(vectorSum)
    val vectorDiff = vector - vector2       // return the difference of two vectors
    println(vectorDiff)
    val vectorScaled = vector * 5           // returns vector scaled 5 times
    println(vectorScaled)
    val vectorReduced = vector / 5          // returns vector reduced 5 time
    println(vectorReduced)
    val vectorDouble = vector / space.Meters(5)    // returns vector reduced and converted to DoubleVector
    println(vectorDouble)
    val dotProduct = vector * vectorDouble  // returns the Dot Product of vector and vectorDouble
    println(dotProduct)

    val crossProduct = vector crossProduct vectorDouble  // currently only supported for 3-dimensional vectors
    println(crossProduct)
```
result

```scala
5.021951811795888 km
QuantityVector(ArrayBuffer(0.2389509188800581 km, 0.8562407926535415 km, 0.45798926118677796 km))
QuantityVector(ArrayBuffer(2.4 km, 8.6 km, 4.6 km))
QuantityVector(ArrayBuffer(0.0 km, 0.0 km, 0.0 km))
QuantityVector(ArrayBuffer(6.0 km, 21.5 km, 11.5 km))
QuantityVector(ArrayBuffer(0.24 km, 0.86 km, 0.45999999999999996 km))
DoubleVector(ArrayBuffer(240.0, 860.0, 459.99999999999994))
5044.0 km
QuantityVector(WrappedArray(0.0 km, 1.1368683772161603E-13 km, 0.0 km))
```
###Money and Price

###参考
[squants](https://github.com/garyKeorkunian/squants)