import app.models.{Employee, Position}
import app.persisatence.EmployeePersistence.{DBTransactor, EmployeePersistence}
import app.persistenceImpl.EmployeePersistenceImpl
import zhttp.http._
import zhttp.service.Server
import zio.blocking.Blocking
import zio.console.putStrLn
import zio.json._
import zio.{ExitCode, URIO, ZEnv, ZIO}

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

  val app = employeeRoutes

  type AppEnv = DBTransactor with EmployeePersistence

  val appEnv = Blocking.live >+> EmployeePersistenceImpl.live

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val program: ZIO[AppEnv, Throwable, Unit] = for {
      _ <- EmployeePersistenceImpl.createEmployeeTable
      _ <- Server.start(8090, app)
    } yield ()

    program
      .provideSomeLayer[ZEnv](appEnv)
      .tapError(err => putStrLn(s"Execution failed with: $err"))
      .exitCode
  }
}