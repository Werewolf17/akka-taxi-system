package com.briskware.taxi.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.briskware.taxi.model.Location
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class GPSSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("taxi-system"))

  val gps = system.actorOf(Props(classOf[GPS], self), "gps")

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The GPS" must {

    "respond with location when asked" in {

      within(200 millis) {
        gps ! GetLocation
        expectMsgType[LocationResponse]
        gps ! GetLocation
        expectMsgType[LocationResponse]
        expectNoMsg()
      }
    }

    "not respond to invalid message" in {

      within(200 millis) {
        gps ! "FooBar"
        expectNoMsg()
      }
    }

  }

}

