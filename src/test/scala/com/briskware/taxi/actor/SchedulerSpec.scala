package com.briskware.taxi.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class SchedulerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("taxi-system"))

  val tls = system.actorOf(Props(classOf[Scheduler], self, 50 milliseconds, 100 milliseconds), "scheduler")

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "The Scheduler" must {

    "not send events if not requested to start explicitly" in {

      within(500 millis) {
        expectNoMsg()
      }
    }

    "not send events even if StopScheduler is sent" in {

      within(500 millis) {
        tls ! StopScheduler
        expectNoMsg()
      }
    }

    "start sending events when started and stop sending when stopped" in {

      within(500 millis) {
        tls ! StartScheduler
        expectMsg(200 milliseconds, SchedulerFiring)
        expectMsg(200 milliseconds, SchedulerFiring)
        tls ! StopScheduler
        expectNoMsg()
      }
    }

    "not sent events stopped multiple times" in {

      within(300 millis) {
        tls ! StopScheduler
        expectNoMsg(100 millis)
        tls ! StopScheduler
        expectNoMsg(100 millis)
      }
    }

  }

}

