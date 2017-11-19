package org.scorexfoundation.twinschain

trait Calculator {
  val DeltaStep = 1000
  val MaxDelta = 128000


  //find minimal i, that $perSent or less of data is less or equal to i
  def persentile(perSent: Double, data: Seq[Int]): Int = {
    val length = data.length
    def loop(i: Int): Int = {
      val count = data.count(_ <= i)
      if (count.toDouble / length > perSent) {
        i
      } else {
        loop(i + 1)
      }
    }
    loop(0)
  }

  /**
    * Time ago in seconds, where nodes agreed in blocks
    *
    * @return 50% and 90% percentiles
    */
  def calcConsensusDelay(statsLines: Seq[Seq[(Long, Array[String])]], tails: Seq[Seq[String]], nodeTime: Long): (Long, Long) = {
    val tailsWithTimes: Seq[Seq[(Long, String)]] = tails.indices.map { i =>
      tails(i).map { b =>
        val firstAppears = statsLines(i).find(_._2.contains(b)).get
        (firstAppears._1, b)
      }
    }

    def calcDelta(delta: Long, minSuccess: Long, maxFailure: Long, pc: Double): Long = {

      assert(delta > maxFailure && delta < minSuccess && delta >= 0)
      val lastBlockDeltaBack: Seq[String] = tailsWithTimes.map(t => t.filter(_._1 <= (nodeTime - delta)).last._2)
      //per cent of nodes that agrees on block delta milliseconds ago
      val percents: Seq[Double] = lastBlockDeltaBack.map(lb => tails.count(_.contains(lb)).toDouble / tails.length)
      if (percents.count(_ >= pc).toDouble / percents.length >= pc) {
//                println(s"found $delta for $pc")
        if (minSuccess - delta > DeltaStep && delta > 0) {
          calcDelta((maxFailure + delta) / 2, delta, maxFailure, pc)
        } else {
          delta
        }
      } else {
//                println(s"failed $delta for $pc")
        if (minSuccess - delta > DeltaStep) {
          calcDelta((minSuccess + delta) / 2, minSuccess, delta, pc)
        } else {
          minSuccess
        }
      }
    }
    val delta50 = calcDelta(0, MaxDelta, -DeltaStep, 0.5)
    val delta90 = calcDelta(32000, MaxDelta, 0, 0.9)
    (delta50, delta90)
  }

  /**
    * find last row with timestamp <= time
    */
  def getTail(statsLines: Seq[(Long, Array[String])], time: Long): (Long, Array[String]) = {
    val index = statsLines.indices.find(i => statsLines(i)._1 <= time && statsLines(i + 1)._1 > time).get
    statsLines(index)
  }

  /**
    *
    * @return 50% and 90% percentiles
    */
  def calcBlockDiff(tails: Seq[Seq[String]]): (Int, Int) = {
    val diffs = tails.map { t =>
      tails.map(_.count(s => !t.contains(s)))
    }
    val persentiles: Seq[(Int, Int)] = diffs.map { d =>
      (persentile(0.5, d), persentile(0.9, d))
    }
    (persentile(0.5, persentiles.map(_._1)), persentile(0.9, persentiles.map(_._2)))
  }

}
