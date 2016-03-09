package com.oneapm.arkenstone.actors

import akka.actor._
import akka.pattern._
import akka.testkit._
import akka.util.Timeout
import com.oneapm.arkenstone.actors.DbActor.{Query, Row}
import org.scalatest._
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zhxiaog on 16/3/8.
  */
class DbActorTest
  extends TestKit(ActorSystem("DbActorTest")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll
{

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val timeout = Timeout(10 seconds)

  "DbActor" should {
    "persist row" in {
      val actor = system.actorOf(DbActor.props)

      actor ! Row("test")

      Await.result((actor ? Query("test")).mapTo[Long], 10 seconds) shouldBe >=(1L)
    }
  }
}
