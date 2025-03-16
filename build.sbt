import Dependencies._

ThisBuild / scalaVersion := "3.3.3"
ThisBuild / version := "0.6.0"
ThisBuild / versionScheme := Some("early-semver")

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

// Common settings for both projects
lazy val commonSettings = Seq(
  organization := "io.github.ollls",
  libraryDependencies += scalaTest % Test,
  libraryDependencies += "io.github.ollls" %% "zio-quartz-h2" % "0.6.0",
  libraryDependencies += "dev.zio" %% "zio" % "2.1.16",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-no-indent"
  )
)

// Main project that aggregates the two subprojects
lazy val root = (project in file("."))
  .aggregate(nio, iouring)
  .settings(
    name := "zio-qh2-examples",
    publish / skip := true
  )

// NIO project using standard Java NIO
lazy val nio = (project in file("NIO"))
  .settings(
    commonSettings,
    name := "zio-qh2-nio-example",
    Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala",
    Compile / mainClass := Some("example.nio.NioApp")
  )

// IOURING project using Linux IO-Uring
lazy val iouring = (project in file("IOURING"))
  .settings(
    commonSettings,
    name := "zio-qh2-iouring-example",
    Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala",
    Compile / mainClass := Some("example.iouring.IouringApp")
  )
