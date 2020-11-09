import Dependencies._

lazy val options = Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ymacro-annotations" // for newtype and simulacrum
)

lazy val commonSettings = Seq(
  name := "prueba-s4n-scala",
  version := "0.1",
  organization := "com.s4n",
  scalaVersion := "2.13.0",
  scalacOptions := options,
  scalaSource in Test := baseDirectory.value / "src/test/scala",
  scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala",
  scalafmtOnCompile in ThisBuild := true,
  autoCompilerPlugins in ThisBuild := true,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", _ @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings))
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .configs(Test)
  .settings(inConfig(Test)(Defaults.testSettings))
  .settings(
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        monix("monix-eval"),
        monix("monix-execution"),
        fs2("fs2-core"),
        fs2("fs2-io"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        scalaTest % Test
      )),
    mainClass in assembly := Some("com.s4n.main.Main"),
    assemblyJarName in assembly := "main.jar"
  ).aggregate(infrastructure, core, test, delivery, location)
  .dependsOn(
    core,
    test,
    file_infra,
    delivery_domain,
    delivery_adapter,
    delivery_application,
    location_domain,
    location_adapter,
    location_application
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    scalacOptions ++= options,
    libraryDependencies += newType
  ).dependsOn(test)

lazy val delivery = (project in file("delivery-management"))
  .settings(commonSettings: _*)
  .settings(
    name := "delivery-management"
  ).aggregate(
  delivery_domain,
  delivery_adapter,
  delivery_application
)

lazy val delivery_domain = (project in file("delivery-management/domain"))
  .settings(commonSettings: _*)
  .settings(
    name := "delivery-management-domain",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        fs2("fs2-core"),
        scalaTest % Test
      ))
  ).dependsOn(core, test)

lazy val delivery_adapter = (project in file("delivery-management/adapter"))
  .settings(commonSettings: _*)
  .settings(
    name := "delivery-management-adapter",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        refined("refined"),
        refined("refined-cats"),
        ciris("ciris"),
        ciris("ciris-refined"),
        ciris("ciris-enumeratum"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        simulacrum,
        scalaTest % Test
      ))
  ).dependsOn(core, test, file_infra, delivery_domain)

lazy val delivery_application = (project in file("delivery-management/application"))
  .settings(commonSettings: _*)
  .settings(
    name := "delivery-management-application",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        refined("refined"),
        refined("refined-cats"),
        ciris("ciris"),
        ciris("ciris-refined"),
        ciris("ciris-enumeratum"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        simulacrum,
        scalaTest % Test
      ))
  ).dependsOn(
    core, test, delivery_domain, location_application
  )

lazy val location = (project in file("location-management"))
  .settings(commonSettings: _*)
  .settings(
    name := "location-management"
  ).aggregate(
    location_domain,
    location_adapter,
    location_application
  )

lazy val location_domain = (project in file("location-management/domain"))
  .settings(commonSettings: _*)
  .settings(
    name := "location-management-domain",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        fs2("fs2-core"),
        scalaTest % Test
      ))
  ).dependsOn(core, test)

lazy val location_adapter = (project in file("location-management/adapter"))
  .settings(commonSettings: _*)
  .settings(
    name := "location-management-adapter",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        refined("refined"),
        refined("refined-cats"),
        ciris("ciris"),
        ciris("ciris-refined"),
        ciris("ciris-enumeratum"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        simulacrum,
        scalaTest % Test
      ))
  ).dependsOn(core, test, file_infra, location_domain)

lazy val location_application = (project in file("location-management/application"))
  .settings(commonSettings: _*)
  .settings(
    name := "location-management-application",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        refined("refined"),
        refined("refined-cats"),
        ciris("ciris"),
        ciris("ciris-refined"),
        ciris("ciris-enumeratum"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        simulacrum,
        scalaTest % Test
      ))
  ).dependsOn(core, test, location_domain)

lazy val test = (project in file("test"))
  .settings(commonSettings: _*)
  .settings(
    name := "test",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        scalaTest
      ))
  )

lazy val infrastructure = (project in file("infrastructure"))
  .settings(commonSettings: _*)
  .settings(name := "infrastructure")
  .aggregate(file_infra)

lazy val file_infra = (project in file("infrastructure/file"))
  .settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings))
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(
    name := "infrastructure-file",
    scalacOptions ++= options,
    libraryDependencies ++= (
      Seq(
        cats("cats-macros"),
        cats("cats-kernel"),
        cats("cats-core"),
        cats("cats-effect"),
        fs2("fs2-core"),
        fs2("fs2-io"),
        logback("logback-classic"),
        log4cats("log4cats-core"),
        log4cats("log4cats-slf4j"),
        simulacrum,
        scalaTest % Test
      ))
  ).dependsOn(test)
