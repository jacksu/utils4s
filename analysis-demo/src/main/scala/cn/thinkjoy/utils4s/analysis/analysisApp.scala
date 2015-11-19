package cn.thinkjoy.utils4s.analysis

import squants.energy.Power
import squants.energy._
import squants.space._
import squants._
import squants.time._
import squants.market._

/**
 * Created by jacksu on 15/11/16.
 */

object analysisApp {
  def main(args: Array[String]) {
    val load1: Power = Kilowatts(12) // returns Power(12, Kilowatts) or 12 kW
    val load2: Power = Megawatts(0.023) // Power: 0.023 MW
    val sum = load1 + load2 // Power: 35 kW - unit on left side is preserved
    println("%06.2f".format(sum.toMegawatts))
    val ratio = Days(1) / Hours(3)
    println(ratio)
    val seconds = (Hours(2) + Days(1) + time.Seconds(1)).toSeconds
    println(seconds)
    println(Days(1).toSeconds)

    //toString
    println(Days(1) toString time.Seconds)

    //totuple
    println(Days(1) toTuple time.Seconds)

    //Approximations
    implicit val tolerance = Watts(.1)      // implicit Power: 0.1 W
    val load = Kilowatts(2.0)               // Power: 2.0 kW
    val reading = Kilowatts(1.9999)
    println(load =~ reading)

    //vectors
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

    //money
    val tenBucks = USD(10)
    println(tenBucks)
    val tenyuan = CNY(10)
    println(tenyuan)
    val hongkong = HKD(10)
    println(hongkong)

    //price
    val energyPrice = USD(102.20) / MegawattHours(1)
    println(energyPrice)
  }
}
