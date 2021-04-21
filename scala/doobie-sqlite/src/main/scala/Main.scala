import cats.effect._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success)

  val program: IO[Unit] = for {
    command <- IO(io.StdIn.readLine())
    result <- CMD.run(command)
    _ <- IO(result match {
      case Left(e) => e.printStackTrace()
      case Right(v) => println(">> " + v)
    })
    _ <- if(command.isEmpty) IO.unit else program
  } yield ()

  // SIGTERM works
  // SIGINT not work. why??
  sys.addShutdownHook {
    // store in-memory cache
  }
}
