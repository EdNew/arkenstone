package com.oneapm.arkenstone

import akka.actor._
import akka.pattern._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.oneapm.arkenstone.actors.DbActor.Query

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object QueryAPI extends QueryAPI
{
  var actorRef: ActorRef = null

  def apply(actorRef: ActorRef) = {
    this.actorRef = actorRef
    this
  }

  override def dbAcotr: ActorRef = this.actorRef
}

/**
  * Created by zhxiaog on 16/3/8.
  */
trait QueryAPI
{

  def dbAcotr: ActorRef

  implicit val timeout = Timeout(10 seconds)

  val routes =
    path("api" / "v1" / "query") {
      parameter("k".as[String]) { k =>
        complete {
          (dbAcotr ? Query(k))
            .mapTo[Long]
            .map { idx =>
              val body = HttpEntity(contentType = ContentTypes.`application/json`, string = """{"id":1}""")
              HttpResponse(StatusCodes.OK, entity = body)
            }
        }
      }
    }
}
