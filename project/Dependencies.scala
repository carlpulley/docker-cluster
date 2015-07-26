import sbt._
import sbt.Keys._

object Dependencies {

  val scala = "2.11.7"

  object akka {
    val version = "2.4-M2"

    val actor                 = "com.typesafe.akka"   %% "akka-actor"                    % version
    val cluster               = "com.typesafe.akka"   %% "akka-cluster"                  % version
    val contrib               = "com.typesafe.akka"   %% "akka-contrib"                  % version intransitive()
    val remote                = "com.typesafe.akka"   %% "akka-remote"                   % version
    val slf4j                 = "com.typesafe.akka"   %% "akka-slf4j"                    % version
    val testkit               = "com.typesafe.akka"   %% "akka-testkit"                  % version
  }

  object scalaz {
    val version = "7.1.3"

    val core = "org.scalaz" %% "scalaz-core"  % version
  }

  val logback          = "ch.qos.logback"         %  "logback-classic"       % "1.1.3"
  val scalacheck       = "org.scalacheck"         %% "scalacheck"            % "1.12.4"
  val scalatest        = "org.scalatest"          %% "scalatest"             % "2.2.5"
  val typesafe         = "com.typesafe"           %  "config"                % "1.3.0"

}
