package com.briskware.taxi.actor

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import com.briskware.taxi.model.Location

case class LocationReport(loc: Location)

class ManagementCentre(
  val owner: ActorRef,
  val numberOfTaxis: Int,
  val tubeLocationService: Option[ActorRef] = None) extends Actor with ActorLogging {

  /**
   * Create a number of Taxi child actors
   */
  override def preStart() = {
    val tubeLocationServicePath = tubeLocationService map { _.path.toStringWithoutAddress }
    for (i <- 1 to numberOfTaxis) {
      context.actorOf(Props(classOf[Taxi], self, tubeLocationServicePath), s"taxi-$i")
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
