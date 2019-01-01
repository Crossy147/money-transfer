enablePlugins(JavaAppPackaging)

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name in ThisBuild := "money-transfer",
  scalaVersion in ThisBuild := "2.12.8",
  version in ThisBuild := "0.1.0",
  description in ThisBuild := "Simple money transferring system with REST api",
  libraryDependencies ++= Dependencies.backend,
)

lazy val root = (project in file("."))
    .settings(commonSettings: _*)