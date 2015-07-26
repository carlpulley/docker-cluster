import sbt._
import Keys._
import Dependencies._
import NativePackagerHelper._

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

Project.settings

name := "docker-cluster"

libraryDependencies ++= Seq(
  // Core Akka
  akka.actor,
  akka.cluster,
  akka.contrib,
  akka.slf4j,
  // Miscellaneous
  logback,
  scalaz.core,
  typesafe,
  // Testing
  scalatest     % "test",
  scalacheck    % "test",
  akka.testkit  % "test"
)

mainClass in Compile := Some("cakesolutions.Main")

maintainer in Docker := "Carl Pulley <carlp@cakesolutions.net>"

dockerBaseImage := "java:8"

bashScriptConfigLocation := Some("${app_home}/../resources/application.conf")

mappings in Universal ++= directory("src/main/resources")
