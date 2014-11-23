package com.briskware.taxi.actor

import akka.util.Timeout

import language.postfixOps
import scala.concurrent.duration._

import akka.actor._
import akka.pattern.ask
import com.briskware.taxi.model.Location

import scala.util.Success

/*
 * Taxi Actor
 */
class Taxi(val owner: ActorRef, val tubeLocationServicePath: Option[String]) extends Actor with ActorLogging {

  implicit val dispatcher = context.system.dispatcher

  /// creating supervised actors
  val gps = context.actorOf(Props(classOf[GPS], self), s"gps-for-${self.path.name}")
  val scheduler = context.actorOf(Props(classOf[Scheduler], self, 50 milliseconds, 50 milliseconds), s"scheduler-for-${self.path.name}")

  // looked-up unsupervised actors
  val tubeLocationService = tubeLocationServicePath map { context.actorSelection(_) }

  override def preStart(): Unit = {
    super.preStart()
    scheduler ! StartScheduler
  }

  override def receive = {
    case SchedulerFiring => gps ! GetLocation
    case LocationResponse(loc) => handleLocationResponse(loc)
  }

  /**
   * Delegating to the Tube Location Service (TLS) - if defined, to compute if the location is near the a Tube Station.
   * This is done via a Future in order to preserve the context. This would not have been necessary, since we are reporting
   * back to the parent of this Actor (the management centre), however
   * the code below just shows a different way of interacting with Actors and I wanted to demonstrate it here.
   */
  private def handleLocationResponse(loc: Location) = tubeLocationService match {
    // if TLS has been defined then check for Tube proximity
    // and send message only if the location is near a Tube station
    case Some(ref) =>
      implicit val dispatcher = context.dispatcher
      implicit val timeout = Timeout(1 second)
      val isCloseFuture = ref ? CloseToTubeStation(loc)
      isCloseFuture onComplete {
        case Success(CloseToTubeStationResponse(isClose)) =>
          if (isClose) owner ! LocationReport(loc)
        case _ => //NOP
      }
    // if there is no TLS, then always send the report
    case _ => owner ! LocationReport(loc)
  }

}


