import io.getquill._

object Main extends App {
  case class Person(id: Long, name: String, age: Int)


  lazy val ctx = new SqliteJdbcContext(SnakeCase, "ctx")
  import ctx._

  val create = """
    CREATE TABLE IF NOT EXISTS person (
      id    INTEGER   PRIMARY KEY   AUTOINCREMENT,
      name  TEXT      NOT NULL,
      age   NUMERIC   NOT NULL
    )
    """.stripMargin
  println(executeAction(create))

  println(
    ctx.run(quote(query[Person].delete))
  )


  val a = quote {
    liftQuery(List(
      Person(0, "John",   31),
      Person(0, "Smith",  32),
      Person(0, "Sthing", 33)
    )).foreach(p => query[Person].insert(p).returningGenerated(_.id))
  }
  ctx.run(a)


  val p = quote {
    query[Person].filter(_.age > 30)
  }


  println(ctx.run(p).mkString(", "))
}
