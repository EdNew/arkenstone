package com.oneapm.arkenstone

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives._
import com.oneapm.arkenstone.actors.DbActor.Row
import spray.json._

import scala.collection.immutable

object IngestionAPI extends IngestionAPI
{
  var actorRef: ActorRef = null

  def apply(actorRef: ActorRef) = {
    this.actorRef = actorRef
    this
  }

  override def dbActor: ActorRef = this.actorRef
}

/**
  * Created by zhxiaog on 16/3/7.
  */
trait IngestionAPI
{

  def dbActor: ActorRef

  val routes =
    path("api" / "v1" / "events") {
      post {
        entity(as[JsValue]) {
          js =>
            complete {
              js.asJsObject.getFields("name", "host", "process") match {
                case Seq(JsString(name), JsString(host), process) => {

                  val key = s"${name}:${host}:${process}"
                  dbActor ! Row(key)

                  val location = s"/api/v1/query?k=${name}:${host}:${process}"
                  HttpResponse(status = StatusCodes.Created, headers = immutable.Seq(Location(location)))
                }
                case _ => HttpResponse(status = StatusCodes.BadRequest)
              }
            }
        }
      }
    }
}
