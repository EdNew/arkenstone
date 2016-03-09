package com.oneapm.arkenstone

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.oneapm.arkenstone.actors.DbActor
import org.scalatest.{Matchers, WordSpec}
import akka.testkit._

/**
  * see http://blog.madhukaraphatak.com/akka-http-testing/
  *
  * Created by zhxiaog on 16/3/7.
  */
class IngestionAPITest
  extends WordSpec with Matchers with ScalatestRouteTest with IngestionAPI
{

  "data ingestion API(v1)" should {

    "POST to /api/v1/events results 201" in {
      val jsonRequest =
        """
          |{
          | "name": "zhxiaog-pc",
          | "host": "127.0.0.1",
          | "process": 2048
          |}
        """.stripMargin

      lazy val body = HttpEntity(contentType = ContentTypes.`application/json`, string = jsonRequest)
      val request = Post("/api/v1/events", body)

      request ~> routes ~> check {
        status shouldEqual (StatusCodes.Created)
        header("Location").map(h => h.value) shouldEqual (Some("/api/v1/query?k=zhxiaog-pc:127.0.0.1:2048"))

        dbActor
      }
    }
  }

  override def dbActor: ActorRef = TestActorRef[DbActor]
}
