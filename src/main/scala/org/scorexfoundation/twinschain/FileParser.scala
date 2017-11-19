package org.scorexfoundation.twinschain

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object FileParser extends App with Calculator {

  val nameS = "/home/pozharko/Code/StatsCollector/data/R0<PARAM>.stats"

  Seq(2, 4, 6, 8).foreach{ i =>
    newFormatParser(nameS.replace("<PARAM>", i.toString))
  }

  def newFormatParser(filename: String): Unit = {
    var per90: ArrayBuffer[Int] = ArrayBuffer[Int]()
    var per50: ArrayBuffer[Int] = ArrayBuffer[Int]()
    for (line <- Source.fromFile(filename).getLines()) {
      val values: Seq[String] = line.replace(",|,", ",").split(",")
      per90 += values.last.toInt
      per50 += values(values.length - 2).toInt
    }
    println(persentile(0.5, per50) + "," + persentile(0.9, per90))
  }

  def oldFormatParser(filename: String): Unit = {
    for (line <- Source.fromFile(filename).getLines()) {
      val values: Seq[Int] = line.split(",").map(_.toInt)
      val per50 = (0 until values.length / 2) map (i => values(i * 2))
      val per90 = (0 until values.length / 2) map (i => values(i * 2 + 1))
      println(per50.mkString(",") + ",|," + per90.mkString(",") + ",|," +
        persentile(0.5, per50) + ",|," + persentile(0.9, per90))
    }
  }

}
