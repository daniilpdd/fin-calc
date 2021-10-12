import models.{Employee, Position}
import zhttp.http._
import zhttp.service.Server
import zio.json._
import zio.{ExitCode, URIO}

import scala.collection.mutable.ListBuffer

object Main extends zio.App {
  val positions = ListBuffer[Position]()
  val employees = ListBuffer[Employee]()

  val employeeRoutes = HttpApp.collect {
    case req @ Method.POST -> a / "employee" =>
      req.getBodyAsString.map(_.fromJson[Employee]).get match {
        case Left(value) => Response.jsonString(s"""{"message": "$value"}""")
        case Right(empl) =>
          employees.addOne(empl)
          Response.jsonString("""{"message": "Employee adding success!"}""")
      }
    case Method.GET -> a / "employee" => Response.jsonString(employees.toJson)
  }

  val positionRoutes = HttpApp.collect {
    case req @ Method.POST -> a / "positions" =>
      req.getBodyAsString.map(_.fromJson[Position]).get match {
        case Left(value) => Response.jsonString(s"""{"message": "$value"}""")
        case Right(empl) =>
          positions.addOne(empl)
          Response.jsonString("""{"message": "Position adding success!"}""")
      }
    case Method.GET -> a / "positions" => Response.jsonString(positions.toJson)
  }

  val app = employeeRoutes +++ positionRoutes

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8090, app).exitCode
}