import sbt._
import Keys._

object finaglezk extends Build {
  val finagleVersion = "6.25.0"
  val clientVersion = "0.2.0"

  lazy val core = Project(
    id = "finagle-zookeeper-core",
    base = file("core"),
    settings = baseSettings ++ buildSettings ++ Seq(name += "-core")
  )

  lazy val example = Project(
    id = "finagle-zookeeper-example",
    base = file("example"),
    settings = buildSettings ++ Seq(name += "-example")
  ).dependsOn(core)

  lazy val integration = Project(
    id = "finagle-zookeeper-integration",
    base = file("integration"),
    settings = testSettings ++ buildSettings ++ Seq(name += "-integration")

  ).dependsOn(core, example)

  lazy val root = Project(
    id = "finagle-zookeeper",
    base = file("."),
    settings = Project.defaultSettings ++ buildSettings ++ Seq(
      publishLocal := {},
      publish := {}
    ),
    aggregate = Seq(core)
  ) 


  lazy val baseSettings = Seq(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4",
      "com.twitter" %% "finagle-core" % finagleVersion,
      "junit" % "junit" % "4.12",
      "org.mockito" % "mockito-all" % "1.10.19" % "test"
    )
  )

  lazy val buildSettings = Seq(
    name := "finagle-zookeeper",
    organization := "com.twitter",
    version := clientVersion,
    scalaVersion := "2.11.6",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature","-Xmax-classfile-name", "254")
  )

  lazy val runTests = taskKey[Unit]("Runs configurations and tests")
  lazy val testSettings = Seq(
    runTests := IntegrationTest.integrationTestTask(testOnly in Test).value,
    parallelExecution := false,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature","-Xmax-classfile-name", "254")
  )
}
