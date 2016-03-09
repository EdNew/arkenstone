package com.oneapm.arkenstone

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestProbe
import com.oneapm.arkenstone.actors.DbActor.{Query, Row}
import org.scalatest._
import org.scalatest.words.ShouldVerb
import spray.json.{JsNumber, JsValue}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by zhxiaog on 16/3/8.
  */
class QueryAPITest
  extends WordSpec with ScalatestRouteTest with Matchers with ShouldVerb with QueryAPI
{

  "Query API(v1)" should {
    "Get /api/v1/query?k=zhxiaog-pc:127.0.0.1:2048 returns positive integers" in {

      val request = Get("/api/v1/query?k=zhxiaog-pc:127.0.0.1:2048")

      val result = request ~> routes ~> runRoute

      testProbe.within(10 seconds) {
        testProbe.expectMsg(Query("zhxiaog-pc:127.0.0.1:2048"))
        testProbe.reply(1L)
      }

      result ~> check {
        status shouldEqual (StatusCodes.OK)
        val idx: BigDecimal = responseAs[JsValue].asJsObject.getFields("id") match {
          case Seq(JsNumber(idx)) => idx
          case _ => -1
        }

        idx shouldBe >=(BigDecimal(0))
      }
    }
  }

  lazy val testProbe = TestProbe()

  override def dbAcotr: ActorRef = testProbe.ref
}
