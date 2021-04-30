
name := "quill-sqlite"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "org.xerial" % "sqlite-jdbc" % "3.34.0",
  "io.getquill" %% "quill-jdbc" % "3.7.0",

  "org.scalameta" %% "munit" % "0.7.23" % Test
)

testFrameworks += new TestFramework("munit.Framework")

scalacOptions ++= Seq(
  "-deprecation",
  "-language:higherKinds",
  "-language:postfixOps",
  "-Xfatal-warnings",
)
