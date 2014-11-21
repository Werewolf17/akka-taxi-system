package com.briskware.taxi.actor

import akka.actor.{Props, ActorLogging, Actor}
import com.briskware.taxi.model.Location

case class LocationReport(loc: Location)

class ManagementCentre(val numberOfTaxis: Int) extends Actor with ActorLogging {

  val tubeLocationService = context.actorOf(Props[TubeLocationService], "tube-location-service")

  override def preStart() = {
    for (i <- 1 to numberOfTaxis) {
      context.actorOf(Props[Taxi], s"Taxi-$i")
    }
    super.preStart()
  }

  override def receive: Receive = {
    case LocationReport(loc) => log.info(s"Received ${loc} from ${sender.path.name}")
  }

  override def postStop(): Unit = {
    super.postStop()
    context.system.shutdown()
  }
}
