package org.scorexfoundation

import io.circe.parser._
import io.circe.{HCursor, Json}

import scalaj.http.Http


object ApiClient {

  /**
    * Get ids of last length blocks from peer
    */
  def getTail(peer: String, port: Int, length: Int): Seq[String] = {
    val resp = getRequest(peer, port, "/stats/tail/" + length)
    val doc: Json = parse(resp).getOrElse(Json.Null)
    val cursor: HCursor = doc.hcursor
    cursor.downField("data").downField("tail").as[List[String]].right.get
  }

  private def getRequest(peer: String, port: Int, route: String): String = {
    Http("http://" + peer + ":" + port + route).header("content-type", "application/json").asString.body
  }

}
