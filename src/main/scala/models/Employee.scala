package models

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Employee(fio: String, position: Position)

object Employee {
  implicit val decoder: JsonDecoder[Employee] = DeriveJsonDecoder.gen[Employee]
  implicit val encoder: JsonEncoder[Employee] = DeriveJsonEncoder.gen[Employee]
}