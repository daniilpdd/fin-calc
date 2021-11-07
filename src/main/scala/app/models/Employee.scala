package app.models

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Employee(fio: String, positionId: Int)

final case class EmployeeNotFound(id: Int) extends Exception

object Employee {
  implicit val decoder: JsonDecoder[Employee] = DeriveJsonDecoder.gen[Employee]
  implicit val encoder: JsonEncoder[Employee] = DeriveJsonEncoder.gen[Employee]
}