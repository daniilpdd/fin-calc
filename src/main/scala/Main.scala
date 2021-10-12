import zhttp.http._
import zhttp.service.Server
import zio.{ExitCode, URIO}

object Main extends zio.App {

  val app: HttpApp[Any, Nothing] = HttpApp.collect {
    case Method.GET -> a / "text" => Response.text("Hello World!")
    case Method.GET -> a / "json" => Response.jsonString("""{"greetings": "Hello World!"}""")
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = Server.start(8090, app.silent).exitCode
}
