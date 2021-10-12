package models

import zio.json._

case class Position(name: String, monthRate: Double)

object Position {
  implicit val decoder: JsonDecoder[Position] = DeriveJsonDecoder.gen[Position]
  implicit val encoder: JsonEncoder[Position] = DeriveJsonEncoder.gen[Position]
}