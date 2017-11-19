package org.scorexfoundation.ethereum

import org.scorexfoundation.twinschain.FileLogger

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object EthAnalyzer extends App {


  //  processDataTouch()
  //  processTransactions()
  mean(s"data/ethereum/txs.csv", 1) // ArrayBuffer(148.2051897720304, 0.7315425355783822, 9165.131383760168)
//  mean(s"data/ethereum/dataLifetime.csv", 60 * 4) //ArrayBuffer(23731, 0.0, 0.0)



  def mean(filename: String, min: Int = 0): Unit = {
    val buff: ArrayBuffer[Long] = ArrayBuffer(0, 0, 0)
    var c = 0
    var c1 = 0
    Source.fromFile(filename).getLines().foreach { l =>
      val r = l.split(",")
      if (!r.head.startsWith("size") && r.forall(_.toLong >= min)) {
        r.indices.foreach { i =>
          val v = r(i).toLong
          assert(v >= min)
          buff(i) += v
        }
        c = c + 1
      } else {
        c1 = c1 + 1
      }
    }
    println(c1 + " | " + c + " | " + buff.map(_.toDouble / c))
  }


  def processDataTouch(): Unit = {
    val rows: TrieMap[String, (Long, Option[Long])] = TrieMap()
    Source.fromFile(s"data/ethereum/dataTouch.csv").getLines().foreach { l =>
      val r = l.split(",")
      val key: String = r.head
      val height: Long = r.last.toLong
      assert(height > 46000, s"?? $l | ${r.last} | $height")
      rows.get(key) match {
        case None =>
          rows.put(key, (height, None))
        case Some(data) if data._2.isEmpty && height > data._1 =>
          rows.put(key, (data._1, Some(height)))
        case Some(data) =>
      }
    }
    val logger = new FileLogger("data/ethereum/dataLifetimeNonZero.csv")
    logger.clear()
    logger.appendString("size=" + rows.size)
    rows.foreach { r =>
      r._2._2 match {
        case Some(firstTouch) =>
          logger.appendString((firstTouch - r._2._1).toString)
        case None =>
      }
    }
  }

  def processTransactions(): Unit = {
    val logger = new FileLogger("data/ethereum/txs.csv")
    logger.clear()
    logger.appendString(s"sizeBytes,storageBytes,computationCost")

    //height,id,bytes,contractCreation,gasPrice,gasUsed,basicCost,sstoreGas,createGas,otherGas,sstoreBytes,createBytes
    Source.fromFile(s"data/ethereum/ethereumTransactions.csv").getLines().foreach { l =>
      val r = l.split(",")
      if (r.head != "height") {
        val sizeBytes: Long = r(2).toLong
        assert(r(11) == r.last)
        val storageBytes: Long = r(10).toLong + r(11).toLong
        val computationCost: Long = r(9).toLong
        logger.appendString(s"$sizeBytes,$storageBytes,$computationCost")
      }
    }
  }


}
