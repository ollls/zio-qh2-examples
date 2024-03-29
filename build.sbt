import Dependencies._

ThisBuild / scalaVersion := "3.2.1"
ThisBuild / version := "0.4.3"
ThisBuild / versionScheme := Some("strict")

ThisBuild / developers := List(
  Developer(
    id = "ostrygun",
    name = "Oleg Strygun",
    email = "ostrygun@gmail.com",
    url = url("https://github.com/ollls/")
  )
)

ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/ollls/zio-quartz-h2"))
ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
ThisBuild / credentials += Credentials(
  "GnuPG Key ID",
  "gpg",
  "F85809244447DB9FA35A3C9B1EB44A5FC60F4104", // key identifier
  "ignored" // this field is ignored; passwords are supplied by pinentry
)

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/ollls/zio-quartz-h2"),
    "scm:git@github.com:ollls/zio-quartz-h2"
  )
)

Runtime / unmanagedClasspath += baseDirectory.value / "src" / "main" / "resources"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.ollls",
    name := "zio-qh2-examples",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "io.github.ollls" %% "zio-quartz-h2" % "0.5.4",
    libraryDependencies += "dev.zio" %% "zio" % "2.0.5"
    //libraryDependencies += "dev.zio" %% "zio-logging-slf4j" % "2.1.5",
    //libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.4",
    //libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.5"
  )


scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-no-indent"
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
