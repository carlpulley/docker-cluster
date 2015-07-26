import sbt._

object Dependencies {

  val scala = "2.11.7"

  object akka {
    val version = "2.4-M2"

    val actor        = "com.typesafe.akka"         %% "akka-actor"                    % version
    val cluster      = "com.typesafe.akka"         %% "akka-cluster"                  % version
    val contrib      = "com.typesafe.akka"         %% "akka-contrib"                  % version
    val remote       = "com.typesafe.akka"         %% "akka-remote"                   % version
    val sharding     = "com.typesafe.akka"         %% "akka-cluster-sharding"         % version
    val slf4j        = "com.typesafe.akka"         %% "akka-slf4j"                    % version
    val testkit      = "com.typesafe.akka"         %% "akka-testkit"                  % version

    object persistence {
      val core       = "com.typesafe.akka"         %% "akka-persistence-experimental" % version

      object leveldb {
        val core     = "org.iq80.leveldb"          % "leveldb"                        % "0.7"
        val jni      = "org.fusesource.leveldbjni" % "leveldbjni-all"                 % "1.8"
      }

      object cassandra {
        val core     = "com.github.krasserm"       %% "akka-persistence-cassandra"    % "0.3.4"
        val driver   = "com.datastax.cassandra"    % "cassandra-driver-core"          % "2.1.7.1"
      }
    }
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
