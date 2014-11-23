package com.briskware.taxi

import language.postfixOps
import scala.concurrent.duration._

import akka.actor._
import com.briskware.taxi.actor.{TubeLocationService, ManagementCentre}

object Boot extends App {

  val system = ActorSystem("taxi-system")
  implicit val dispatcher = system.dispatcher
  
  val main = system.actorOf(Props[Boot], "boot")

  // shut down the top level actors once we feel it has run long enough
  system.scheduler.scheduleOnce(30 seconds, main, PoisonPill)
  system.awaitTermination(35 seconds)

}

class Boot extends Actor with ActorLogging {

  override def preStart(): Unit = {
    super.preStart()
    // create the tube location service - this is injected to the management centre further down
    val tubeLocationService = context.actorOf(Props(classOf[TubeLocationService], self), "tube-location-service")
    // create the management centre - this kicks off the whole processing
    context.actorOf(Props(classOf[ManagementCentre], self, 1000, Some(tubeLocationService)), "management-centre")
  }

  override def receive: Receive = {
    case msg @ _ => log.info(s"Main Received ${msg}")
  }

}
