val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "fastcsv4s3",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "de.siegmar" % "fastcsv" % "3.6.0",
      "org.typelevel" %% "shapeless3-deriving" % "3.5.0",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test

    )
    
  )
