val scala3Version = "3.6.4"

lazy val root = (project in file("."))
  .aggregate(core, fs2)
  .settings(
    name := "fastcsv4s3",
    scalaVersion := "3.6.4",
    publish / skip := true  // ルートプロジェクトは公開対象外
  )

lazy val core = (project in file("core"))
  .settings(
    name := "fastcsv4s3-core",
    scalaVersion := "3.6.4",
    scalacOptions ++= Seq("-Xmax-inlines", "128"),
    libraryDependencies ++= Seq(
      "de.siegmar" % "fastcsv" % "3.6.0",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val fs2 = (project in file("fs2"))
  .dependsOn(core)
  .settings(
    name := "fastcsv4s3-fs2",
    scalaVersion := "3.6.4",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "co.fs2" %% "fs2-core" % "3.11.0",
      "co.fs2" %% "fs2-io" % "3.11.0",
      "io.github.iltotore" %% "iron-scalacheck" % "3.0.0-RC1",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )