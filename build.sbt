import _root_.sbt.Keys._

organization := "org.scorexfoundation"

name := "statsCollector"

version := "0.1.0"

scalaVersion := "2.12.2"

val circeVersion = "0.+"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.3+",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

licenses := Seq("CC0" -> url("https://creativecommons.org/publicdomain/zero/1.0/legalcode"))

homepage := Some(url("https://github.com/ScorexProject/statsCollector"))

resolvers ++= Seq("Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "SonaType" at "https://oss.sonatype.org/content/groups/public",
  "Typesafe maven releases" at "http://repo.typesafe.com/typesafe/maven-releases/")

mainClass in assembly := Some("org.scorexfoundation.twinschain.LogAnalyzer")
