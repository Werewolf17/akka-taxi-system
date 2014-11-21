package com.briskware.taxi

import language.postfixOps
import scala.concurrent.duration._

import akka.actor.{PoisonPill, Props, ActorSystem}
import com.briskware.taxi.actor.ManagementCentre

object Main extends App {

  val system = ActorSystem("taxi-system")
  implicit val dispatcher = system.dispatcher

  val managementCentre = system.actorOf(Props(classOf[ManagementCentre], 1000), "management-centre")

  // shut down the management centre once we feel it has run long enough
  system.scheduler.scheduleOnce(30 seconds, managementCentre, PoisonPill)
  system.awaitTermination(35 seconds)

}
