
import cats.effect._

import scala.language.implicitConversions
import DB.Data

object CMD {
  private val GET = """GET\s*\(\s*(\w+)\s*\)""".r
  private val SET = """SET\s*\(\s*(\w+)\s*,\s*(\w+)\s*\)""".r

  class IOops[A](io: IO[A]) {
    def debug: IO[A] = {
      io map { a => println(a); a }
    }
  }

  implicit def io2ops[A](io: IO[A]) = new IOops(io)

  def run(command: String): IO[Either[Throwable, String]] = command match {
    case "HELP" =>
      IO.pure(Right("""|/----------------------------------
                       |Commands
                       |--------
                       |  ALL
                       |    - Get all key-value pairs
                       |  GET(key)
                       |    - Get value of given key
                       |  SET(key, value)
                       |    - set value to given key
                       |----------------------------------/
                       |""".stripMargin))

    case "ALL" =>
      DB.all.debug map (_.map(data =>
        if(data.isEmpty) ""
        else
          """List
            |------------------------
            |""".stripMargin +
          data.map({
            case Data(_, k, v, c) => s"($k, $v, $c)"
          }).mkString("\n") +
          "\n------------------------"
      ))

    case GET(key) =>
      DB.get(key).debug map {
        case Left(e) => Left(e)
        case Right(Some(v)) => Right(v)
        case _ => Right(s"[$key] not exist")
      }

    case SET(key, value) =>
      DB.set(key, value).debug map (_.map(_.toString))

    case _ => IO(Right("unknown command"))
  }
}
