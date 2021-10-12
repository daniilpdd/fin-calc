package models

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Publisher(name: String, projectPrice: Double)

object Publisher {
  implicit val decoder: JsonDecoder[Publisher] = DeriveJsonDecoder.gen[Publisher]
  implicit val encoder: JsonEncoder[Publisher] = DeriveJsonEncoder.gen[Publisher]
}