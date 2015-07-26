package cakesolutions

import akka.actor.{ReceiveTimeout, ActorLogging, Actor}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.event.LoggingReceive
import akka.persistence.PersistentActor
import scala.concurrent.duration._

object ClusterNode {

  case class Message(id: String, event: String)

  val shardName = "docker-cluster-node"

  val entityId: ShardRegion.ExtractEntityId = {
    case Message(id, event) =>
      (id, event)
  }

  val shardId: ShardRegion.ExtractShardId = {
    case Message(id, event) =>
      s"${id.hashCode() % 10}"
  }

}

class ClusterNode extends PersistentActor with ActorLogging {

  import ClusterNode._

  log.info("Persistent actor started!")

  context.setReceiveTimeout(context.system.settings.config.getDuration("akka.persistence.passivate", SECONDS).seconds)

  override def persistenceId = "docker-cluster"

  def receiveRecover: Receive = Actor.emptyBehavior

  def receiveCommand: Receive = LoggingReceive {
    case ReceiveTimeout ⇒
      log.debug("Passivation timeout has been triggered")
      context.parent ! Passivate(stopMessage = 'stop)

    case 'stop ⇒
      log.debug("Stopping until an actor recovery is triggered")
      context.stop(self)

    case msg: Message =>
      persist(msg) { event =>
        log.info(s"Received and persisted message $event")
      }
  }

}
