package com.briskware.taxi.actor

import language.postfixOps
import scala.concurrent.duration._

import akka.actor.{ActorRef, Cancellable, ActorLogging, Actor}

/*
 * Scheduler Actor
 */
case object StartScheduler
case object StopScheduler
case object SchedulerFiring

class Scheduler(
  val owner: ActorRef,
  val initialDelay: FiniteDuration,
  val interval: FiniteDuration) extends Actor with ActorLogging {

  implicit val dispatcher = context.system.dispatcher

  var schedule: Option[Cancellable] = None

  override def receive = {
    case StartScheduler =>
      if ( !schedule.isDefined ) {
        log.info("starting")
        schedule = Some(context.system.scheduler.schedule(initialDelay, interval, sender, SchedulerFiring))
      } else {
        log.info("already started!")
      }
    case StopScheduler =>
      schedule map { s =>
        s.cancel()
        log.info("stopped")
      }
      schedule = None
  }
}
