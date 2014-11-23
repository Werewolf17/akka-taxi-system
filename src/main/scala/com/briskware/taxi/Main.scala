package com.briskware.taxi

import language.postfixOps
import scala.concurrent.duration._

import akka.actor._
import com.briskware.taxi.actor.{TubeLocationService, ManagementCentre}

object Main extends App {

  val system = ActorSystem("taxi-system")
  implicit val dispatcher = system.dispatcher

  // create the tube location service - this is injected to the management centre further down
  val tubeLocationService = system.actorOf(Props(classOf[TubeLocationService]), "tube-location-service")
  // create the management centre - this kicks off the whole processing
  val managementCentre = system.actorOf(Props(classOf[ManagementCentre], 1000, Some(tubeLocationService)), "management-centre")

  // shut down the management centre once we feel it has run long enough
  system.scheduler.scheduleOnce(30 seconds, managementCentre, PoisonPill)
  system.awaitTermination(35 seconds)

}
