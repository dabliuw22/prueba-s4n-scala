import sbt._

object Dependencies {
  def cats(artifact: String): ModuleID = "org.typelevel" %% artifact % "2.1.1"
  def monix(artifact: String): ModuleID = "io.monix" %% artifact % "3.2.2"
  def fs2(artifact: String): ModuleID = "co.fs2" %% artifact % "2.4.4"
  def refined(artifact: String): ModuleID = "eu.timepit" %% artifact % "0.9.13"
  def ciris(artifact: String): ModuleID = "is.cir" %% artifact % "1.0.4"
  def log4cats(artifact: String): ModuleID =
    "io.chrisdavenport" %% artifact % "1.1.1"
  def scalaLog(artifact: String): ModuleID =
    "com.typesafe.scala-logging" %% artifact % "3.9.2"
  def logback(artifact: String): ModuleID =
    "ch.qos.logback" % artifact % "1.2.3"
  lazy val newType = "io.estatico" %% "newtype" % "0.4.3"
  lazy val simulacrum: ModuleID = "org.typelevel" %% "simulacrum" % "1.0.1"
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.8"
  lazy val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % "1.14.3"
  lazy val scalaTestPlus: ModuleID = "org.scalatestplus" %% "scalacheck-1-14" % "3.1.0.1"
}
