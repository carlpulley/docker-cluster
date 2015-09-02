import com.typesafe.sbt.packager.docker.ExecCmd
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
  akka.persistence.core,
  akka.persistence.leveldb.core,
  akka.persistence.leveldb.jni,
  akka.sharding,
  akka.slf4j,
  // Miscellaneous
  logback,
  scalaz.core,
  typesafe,
  // Testing
  scalatest    % "test",
  scalacheck   % "test",
  akka.testkit % "test"
)

mainClass in Compile := Some("cakesolutions.Main")

// Dockerfile setup

maintainer in Docker := "Carl Pulley <carlp@cakesolutions.net>"

dockerBaseImage := "java:openjdk-8-jre"

dockerCommands ++= Seq(
  ExecCmd("RUN", "apt-get", "update", "&&", "apt-get", "install", "-y", "curl", "jq")
)

mappings in Universal ++= directory("src/main/resources")

mappings in Universal ++= directory("src/docker/shims")

// Bash boot script setup

bashScriptConfigLocation := Some("${app_home}/../resources/application.conf")

bashScriptExtraDefines += """[ -f ${app_home}/../docker/shims/environment.sh ] && bash ${app_home}/../docker/shims/environment.sh"""
