package cakesolutions

import akka.actor.{Props, ActorSystem}
import akka.cluster.sharding.{ClusterShardingSettings, ClusterSharding}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object Main extends App {

  val config = ConfigFactory.load()
  val clusterName = config.getString("akka.cluster.name")
  val start = s"""["akka.tcp://$clusterName@"""
  val join = s"""", "akka.tcp://$clusterName@"""
  val end = "\"]"
  val seedNodes = config.getString("akka.cluster.seed-nodes").split(",").mkString(start, join, end)

  implicit val system = ActorSystem("DockerCluster", ConfigFactory.parseString(s"akka.cluster.seed-nodes=$seedNodes").withFallback(config))

  import system.dispatcher

  val shard = ClusterSharding(system).start(
    typeName = ClusterNode.shardName,
    entityProps = Props[ClusterNode],
    settings = ClusterShardingSettings(system),
    extractEntityId = ClusterNode.entityId,
    extractShardId = ClusterNode.shardId
  )

  system.log.info("We started!")

  system.scheduler.scheduleOnce(config.getDuration("startup.message.delay", SECONDS).seconds) {
    system.log.info("Sending sample messages to (persistent) sharded cluster actors")

    shard ! ClusterNode.Message("one", "event-one")
    shard ! ClusterNode.Message("two", "event-two")
  }

}
