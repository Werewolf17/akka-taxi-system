package com.briskware.taxi.actor

import akka.actor.{Props, ActorLogging, Actor}
import com.briskware.taxi.model.Location

case class LocationReport(loc: Location)

class ManagementCentre(val numberOfTaxis: Int) extends Actor with ActorLogging {

  val tubeLocationService = context.actorOf(Props[TubeLocationService], "tube-location-service")

  /**
   * Create a number of Taxi child actors
   */
  override def preStart() = {
    for (i <- 1 to numberOfTaxis) {
      context.actorOf(Props[Taxi], s"taxi-$i")
    }
    super.preStart()
  }

  /**
   * Simply receive location reports and log these onto the console.
   */
  override def receive = {
    case LocationReport(loc) => log.info(s"received ${loc} from ${sender.path.name}")
  }

  /**
   * Once this actor is stopped - being root user level, we can shut down the entire actor system
   * and let the application quit.
   */
  override def postStop(): Unit = {
    super.postStop()
    context.system.shutdown()
  }
}
