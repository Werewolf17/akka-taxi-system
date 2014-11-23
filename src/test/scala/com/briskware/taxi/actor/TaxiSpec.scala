package com.briskware.taxi.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TaxiSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("taxi-system"))


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The Taxi with no Tube Location Service" must {
    val taxi = system.actorOf(Props(classOf[Taxi], self, None), "taxi-no-tube-location-service")

    "report every location to the owner" in {

      within(150 millis) {
        expectMsgType[LocationReport](60 millis)
        expectMsgType[LocationReport](60 millis)
      }
    }

  }

  "The Taxi with a Tube Location Service" must {
    val tubeLocationService = system.actorOf(Props(classOf[TubeLocationService]), "tls")
    val taxi = system.actorOf(Props(classOf[Taxi], self, Some(tubeLocationService.path.toStringWithoutAddress)), "taxi-with-tube-location-service")

    "report at least one location within 1 second to the owner" in {

      within(1 second) {
        expectMsgType[LocationReport]
      }
    }

  }

}

