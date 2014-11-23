package com.briskware.taxi.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.briskware.taxi.model.Location

case object GetLocation
case class LocationResponse(loc: Location)

/**
 * GPS Actor
 */
class GPS(val owner: ActorRef) extends Actor with ActorLogging {

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
