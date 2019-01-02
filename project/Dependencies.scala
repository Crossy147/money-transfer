import sbt._

object Dependencies {
  
  object Versions {
    val akkaHttpVersion = "10.1.5"
    val akkaStreamVersion = "2.5.19"
    val slickVersion = "3.2.1"
    val configVersion = "1.3.2"
    val scalaLoggingVersion = "3.9.0"
    val h2DbVersion = "1.3.176"
    val avsCommonsVersion = "1.34.6"
    val scalaTestVersion = "3.0.5"
    val akkaTestkitVersion = "10.1.5"
    val macwireVersion = "2.3.0"
    val sl4jSimpleVersion = "1.7.25"
  }
  
  import Versions._  
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val config = "com.typesafe" % "config" % configVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val h2Db = "com.h2database" % "h2" % h2DbVersion
  val avsCommons = "com.avsystem.commons" %% "commons-core" % avsCommonsVersion
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaTestkitVersion
  val macwire = "com.softwaremill.macwire" %% "macros" % macwireVersion
  val sl4jSimple = "org.slf4j" % "slf4j-simple" % sl4jSimpleVersion

  val backend = Seq(
    akkaHttp,
    akkaStream,
    slick,
    config,
    scalaLogging,
    h2Db,
    avsCommons,
    macwire,
    scalaTest % Test,
    akkaTestkit % Test,
    sl4jSimple,
  )
}
