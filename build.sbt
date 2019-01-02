lazy val commonSettings: Seq[Setting[_]] = Seq(
  name in ThisBuild := "money-transfer",
  scalaVersion in ThisBuild := "2.12.8",
  version in ThisBuild := "0.1.0",
  description in ThisBuild := "Simple money transferring system with REST api",
  libraryDependencies ++= Dependencies.backend,
  parallelExecution in Test := false,
)

lazy val deploymentSettings: Seq[Setting[_]] = Seq(
  assemblyJarName in assembly := s"${name.value}_${scalaVersion.value}_${version.value}.jar",
  mainClass in assembly := Some("ServerMain"),
  test in assembly := {},
)

lazy val root = (project in file("."))
    .settings((commonSettings ++ deploymentSettings): _*)
