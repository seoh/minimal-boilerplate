import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor.Aux

import scala.concurrent.ExecutionContext


object DB {
  import doobie._
  import doobie.implicits._

  implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
     "org.sqlite.JDBC", "jdbc:sqlite:db/test.sqlite", "", ""
  )


  private def now: Long = System.currentTimeMillis

  val create: ConnectionIO[Int] =
    sql"""|
          |CREATE TABLE IF NOT EXISTS data(
          |  id          INTEGER PRIMARY KEY AUTOINCREMENT,
          |  key         TEXT    NOT NULL,
          |  value       TEXT    NOT NULL,
          |  created_at  NUMERIC
          |)
          |""".stripMargin.update.run

  val cleanup: ConnectionIO[Int] =
    sql"""|
          |DELETE FROM data
          |WHERE created_at < ${now - 60*60*1000}
          |""".stripMargin.update.run

  println(
    (for {
      c1 <- create
      c2 <- cleanup
    } yield s"Created($c1), Cleaned-up rows inserted after an hour($c2)")
    .transact(xa)
    .unsafeRunSync()
  )


  type Result[T] = IO[Either[Throwable, T]]
  case class Data(id: Int, key: String, value: String, createdAt: Long)

  /**
   * queries
   */
  def insert(key: String, value: String): ConnectionIO[Long] =
    sql"""|
          |INSERT INTO data (key, value, created_at)
          |VALUES ($key, $value, $now)
          |""".stripMargin
    .update
    .withUniqueGeneratedKeys[Long]("id")

  def select(key: String): ConnectionIO[Option[String]] =
      sql"""|
            |SELECT value FROM data
            |WHERE key == $key
            |  AND created_at > ${now - 60*60*1000}
            |ORDER BY created_at DESC
            |LIMIT 1
            |""".stripMargin
      .query[String]
      .to[List]
      .map (_.headOption)

  val selectAll: ConnectionIO[List[Data]] =
    sql"""|
          |SELECT * FROM data
          |""".stripMargin
    .query[Data]
    .to[List]


  /**
   * effects
   */
  def set(key: String, value: String): Result[Long] =
    insert(key, value).transact(xa).attempt

  def get(key: String): Result[Option[String]] =
    select(key).transact(xa).attempt

  def all: Result[List[Data]] =
    selectAll.transact(xa).attempt
}
