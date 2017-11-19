package org.scorexfoundation.twinschain

import org.scorexfoundation.ApiClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object TwinschainAnalyzer extends App with Settings with Calculator {

  val LastBlockNum = 50

  def loop(): Unit = {
    Future.sequence(Nodes.map { n =>
      Future {
        ApiClient.getTail(n, Port, LastBlockNum)
      }
    }).onComplete {
      case Success(tails) =>
        println(calcBlockDiff(tails))
      case Failure(e) =>
        e.printStackTrace()
    }

    Thread.sleep(1000 * 10)
    loop()
  }

  loop()
}
