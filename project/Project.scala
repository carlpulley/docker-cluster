import sbt._
import Keys._

object Project {

  val settings = Defaults.defaultConfigs ++ Seq(
    organization := "cakesolutions",
    version := "0.1.0-SNAPSHOT",
    resolvers := Resolvers.resolvers,
    scalaVersion := Dependencies.scala,
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-deprecation",
      "-unchecked",
      "-Ywarn-dead-code",
      "-feature"
    ),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    ),
    javaOptions ++= Seq(
      "-Xmx2G"
    )
  )

}
