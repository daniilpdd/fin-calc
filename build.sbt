name := "fin-calc"

version := "0.1"

scalaVersion := "2.13.6"

val DoobieVersion = "0.12.1"
val zioHttpVersion = "1.0.0.0-RC17"
val ZIOInterop = "2.4.0.0"

libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % "1.0.0.0-RC17",
  "dev.zio" %% "zio-json" % "0.1.5",
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.tpolecat" %% "doobie-h2" % DoobieVersion,
  "dev.zio" %% "zio-interop-cats" % ZIOInterop,
)