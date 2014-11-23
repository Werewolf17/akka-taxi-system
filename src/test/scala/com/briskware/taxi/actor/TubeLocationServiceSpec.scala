package com.briskware.taxi.actor

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.{TestKit, ImplicitSender}
import com.briskware.taxi.model.Location
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._

class TubeLocationServiceSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("taxi-system"))

  val tls = system.actorOf(Props(classOf[TubeLocationService], self), "tube-location-service")

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The TubeLocationService actor" must {

    "respond true to a position of N50 E0" in {

      within(200 millis) {
        tls ! CloseToTubeStation(Location(0D,50D))
        expectMsg(CloseToTubeStationResponse(true))
        expectNoMsg
      }
    }

    "respond false to a position of N30 E10" in {
      within(200 millis) {
        tls ! CloseToTubeStation(Location(10D,30D))
        expectMsg(CloseToTubeStationResponse(false))
        expectNoMsg
      }
    }

    "don't respond to a bad message" in {
      within(500 millis) {
        tls ! "Foobar"
        expectNoMsg
      }
    }

  }

}

