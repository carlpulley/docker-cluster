package cakesolutions

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("DockerCluster")

  import system.dispatcher

  val config = system.settings.config
  val store = system.actorOf(Props[SharedLeveldbStore], "store")
  SharedLeveldbJournal.setStore(store, system)

  system.log.info("We started!")

  system.scheduler.scheduleOnce(config.getDuration("startup.message.delay", SECONDS).seconds) {
    system.log.info("Sending sample messages to (persistent) sharded cluster actors")

    val shard = ClusterSharding(system).start(
      typeName = ClusterNode.shardName,
      entityProps = Props(new ClusterNode),
      settings = ClusterShardingSettings(system),
      extractEntityId = ClusterNode.entityId,
      extractShardId = ClusterNode.shardId
    )

    shard ! ClusterNode.Message("one", "event-one")
    shard ! ClusterNode.Message("two", "event-two")
  }

}
