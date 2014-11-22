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
case object ReportLocation

class Taxi extends Actor with ActorLogging {

  implicit val dispatcher = context.system.dispatcher

  /// supervised actors
  val gps = context.actorOf(Props[GPS], s"gps-for-${self.path.name}")
  val scheduler = context.actorOf(Props[Scheduler], s"scheduler-for-${self.path.name}")

  // looked-up unsupervised actors
  lazy val tubeLocationService = context.actorSelection("akka://taxi-system/user/management-centre/tube-location-service")

  override def preStart(): Unit = {
    super.preStart()
    scheduler ! StartScheduler
  }

  override def receive = {
    case ReportLocation => gps ! GetLocation
    case LocationResponse(loc) => handleLocationResponse(loc)
  }

  /**
   * Delegating to the Tube Location Service to compute if the location is near the a Tube Station.
   * This is done via a Future in order to preserve the context. This would not have been necessary, since we are reporting
   * back to the parent of this Actor (the management centre), however
   * the code below just shows a different way of interacting with Actors and I wanted to demonstrate it here.
   */
  private def handleLocationResponse(loc: Location) = {
    implicit val dispatcher = context.dispatcher
    implicit val timeout = Timeout(1 second)
    val isCloseFuture = tubeLocationService ? CloseToTubeStation(loc)
    isCloseFuture onComplete {
      case Success(CloseToTubeStationResponse(isClose)) =>
        if (isClose) context.parent ! LocationReport(loc)
    }
  }

}

/*
 * GPS Actor
 */
case object GetLocation
case class LocationResponse(loc: Location)

private sealed class GPS extends Actor with ActorLogging {

  override def receive = {
    case GetLocation => sender ! LocationResponse(getRandomLocation())
  }

  protected def getRandomLocation(): Location = {
    Location(getRandomNumber(0, 1), getRandomNumber(50, 1))
  }

  protected def getRandomNumber(base: Int, maxOffset: Int) = (base, Math.random() * maxOffset, Math.random()) match {
    case (base, i, sign) if sign < 0.5 => base - i
    case (base, i, _) => base + i
  }

}

/*
 * Scheduler Actor
 */
case object StartScheduler
case object StopScheduler

private sealed class Scheduler extends Actor with ActorLogging {

  implicit val dispatcher = context.system.dispatcher

  var schedule: Option[Cancellable] = None

  override def receive = {
    case StartScheduler =>
      log.info("starting")
      schedule = Some(context.system.scheduler.schedule(1 seconds, 50 milliseconds, sender, ReportLocation))
    case StopScheduler =>
      schedule map { _.cancel() }
      log.info("stopped")
  }

}
