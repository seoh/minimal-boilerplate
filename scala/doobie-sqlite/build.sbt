name := "doobie-sqlite"
scalaVersion := "2.13.5"

val DoobieVersion = "0.12.1"
val SQLiteVersion = "3.34.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "2.5.0",
  "org.xerial" % "sqlite-jdbc" % SQLiteVersion,

  "org.tpolecat" %% "doobie-core"      % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari"    % DoobieVersion,

  "org.scalameta" %% "munit" % "0.7.23" % Test
)

testFrameworks += new TestFramework("munit.Framework")

scalacOptions ++= Seq(
  "-deprecation",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Xfatal-warnings",
)

initialCommands := """
import cats.effect.IO
import cats.effect.ContextShift
import scala.concurrent.ExecutionContext

import doobie._
import doobie.implicits._

implicit val cs: ContextShift[IO] =
  IO.contextShift(ExecutionContext.global)

val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC", "jdbc:sqlite:db/test.sqlite", "", ""
)

type Result[T] = IO[Either[Throwable, T]]
def now: Long = System.currentTimeMillis

case class Data(id: Int, key: String, value: String, createdAt: Long)
"""
