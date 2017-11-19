package org.scorexfoundation.golubev1

import org.scorexfoundation.twinschain.FileLogger

import scala.collection.concurrent.TrieMap
import scala.io.Source

object PoSLeasing extends App {


  var dayStart: Long = 0

  val SecondsInDay: Long = 60 * 60 * 24

  case class Row(time: Long, generator: String, target: Long)

//  maxGenerator("waves", 0.45671917)
//  maxGenerator("nxt", 288337255.toDouble / 998999942)
  logActiveStake("waves",  0.45671917)

  def maxGenerator(blockchain: String, activeStakeFinal: Double) = {
    val map: TrieMap[Long, Seq[Row]] = TrieMap()
    val filename: String = s"/home/pozharko/Code/StatsCollector/data/golubev/${blockchain}.csv"
    Source.fromFile(filename).getLines().foreach { l =>
      if (!l.startsWith("time")) {
        val sp = l.split(",")
        val time = getCorrectTimestamp(sp.head.trim.toLong, blockchain)
        val generator = sp(1).trim
        val target = sp(2).trim.toLong
        val row = Row(time, generator, target)
        val key = row.time / SecondsInDay
        map(key) = map.getOrElse(key, Seq()) :+ row
      }
    }

    val splited: Seq[Seq[Row]] = map.values.toSeq.sortBy(_.head.time)

    val grouped: Seq[(Row, Double, Long)] = splited.map { s =>
      val rows: Seq[(Row, Int)] = s.groupBy(_.generator).map(g => (g._2.head, g._2.size)).toSeq
      val max = rows.maxBy(_._2)
      val sum = s.length
      println(max + " | " + sum)
      val meanTarget = s.map(_.target).map(t => BigInt(t)).sum / s.length
      (max._1, max._2.toDouble / sum, meanTarget.toLong)
    }

    val logger = new FileLogger(s"data/golubev/${blockchain}TopMiner.csv")
    logger.clear()
    logger.appendString(s"Time,Share")
    grouped.foreach { g =>
      logger.appendString(s"${g._1.time},${g._2}")
    }

    val logger3 = new FileLogger(targetFilename(blockchain))
    logger3.clear()
    logger3.appendString(s"Time,Target")
    grouped.foreach { g =>
      logger3.appendString(s"${g._1.time},${g._3}")
    }

    logActiveStake(blockchain, activeStakeFinal)

  }

  def targetFilename(blockchain: String): String = s"data/golubev/${blockchain}Target.csv"


  def logActiveStake(blockchain: String, activeStakeFinal: Double) = {
    val filename = targetFilename(blockchain)
    val logger2 = new FileLogger(s"data/golubev/${blockchain}ActiveStake.csv")
    val targets = Source.fromFile(filename).getLines().toList.tail.map { l =>
      val s = l.split(",")
      (s.head.toLong, s.last.toLong)
    }

    logger2.clear()
    logger2.appendString(s"Time,ActiveStake")
    val coeff = targets.last._2 * activeStakeFinal
    targets.foreach { g =>
      val activeStakeNow =  coeff / g._2
      logger2.appendString(s"${g._1},$activeStakeNow")
    }

  }

  //  Source.fromFile("/home/pozharko/Code/StatsCollector/data/golubev/waves.csv").getLines().foreach { l =>
  //    if(!l.startsWith("time ,generator ,target")) {
  //      val sp = l.split(",")
  //      val time = sp.head.toLong
  //      val generator = sp(1)
  //      val target = sp(2).toLong
  //
  //      if(time > dayStart + MillisecondsInDay) {
  //        dayStart = time
  //      }
  //
  //
  //    }
  //  }

  def getCorrectTimestamp(in: Long, blockchain: String): Long = blockchain match {
    case "waves" => in / 1000
    case "nxt" => in + (1510927317 - 125632917)
  }

}
