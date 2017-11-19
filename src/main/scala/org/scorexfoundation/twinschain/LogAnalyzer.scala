package org.scorexfoundation.twinschain

import java.io.File

import scala.io.Source
import scala.sys.process._
import scala.util.{Failure, Success, Try}

/**
  * Analyze stats from log format
  */
object LogAnalyzer extends App with Calculator with Settings {

  println("Start log analyzer")

  val R = args(0)
  val linesToProcess: Int = Try(args(1).toInt).getOrElse(3000)
  val RootPath = s"data/newLogs/$R"
  new File(RootPath).mkdirs()
  val ResultPath = s"data/stats/$R.stats"
  val TimeStep = 30000

    logDownloader("/home/ubuntu/data/data/tails.data", RootPath)
  calculateStats()

  def calculateStats(): Unit = {
    val initialTime: Long = 1494570210449L - TimeStep
//    val initialTime: Long = {
      val lines: Seq[String] = Source.fromFile(s"$RootPath/${Nodes.last}.stats").getLines().toSeq
//      val r: Seq[(Long, Array[String])] = lines.map(_.split(":")).map(l => (BigInt(l.head).toLong, l.last.split(",")))
//      r(r.length - linesToProcess)._1
//    }

    val statsLines: Seq[Seq[(Long, Array[String])]] = Nodes.map(n => s"$RootPath/$n.stats").map { fn =>
      val lines: Seq[(Long, Array[String])] = Source.fromFile(fn).getLines().toSeq.map(_.split(":"))
        .map(l => (BigInt(l.head).toLong, l.last.split(",")))
      println(fn)
      println(lines.last._1)
      val index = lines.indices.find(i => lines(i)._1 <= initialTime && lines(i + 1)._1 > initialTime).get
      lines.drop(index - 1)
    }
    statsLines.map(_.head).foreach(h => assert(h._1 <= initialTime))
    val logger = new FileLogger(ResultPath)

    logger.clear()
    logger.appendString(s"time,bf50%,bf90%,consensusDelay50%,consensusDelay90%")

    def timeLoop(time: Long): Unit = {
      Try {
        println(s"processing chain at time $time")
        val tails: Seq[Seq[String]] = statsLines.map(a => getTail(a, time)).map(_._2.toSeq)
        val bf = calcBlockDiff(tails)

        val consensusDelay = calcConsensusDelay(statsLines, tails, time)
        logger.appendString(s"${time.toString},${bf._1},${bf._2},${consensusDelay._1},${consensusDelay._2}")
      } match {
        case Success(_) =>
          timeLoop(time + TimeStep)
        case Failure(e) =>
          e.printStackTrace()
      }
    }

    timeLoop(initialTime + MaxDelta)
  }


  def logDownloader(logPath: String, downloadPath: String): Unit = {
    Nodes.foreach { n =>
      val command = s"scp ubuntu@$n:$logPath $downloadPath/$n.stats"
      println(command)
      command.!
    }
  }

}
