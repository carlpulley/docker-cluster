package cakesolutions

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config = ConfigFactory.load()
  val clusterName = config.getString("akka.cluster.name")
  val start = s"""["akka.tcp://$clusterName@"""
  val join = s"""", "akka.tcp://$clusterName@"""
  val end = "\"]"
  val seedNodes = config.getString("akka.cluster.seed-nodes").split(",").mkString(start, join, end)

  implicit val system = ActorSystem("DockerCluster", ConfigFactory.parseString(s"akka.cluster.seed-nodes=$seedNodes").withFallback(config))

  system.log.info("We started!")

}
