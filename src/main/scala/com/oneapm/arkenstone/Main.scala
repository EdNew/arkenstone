package com.oneapm.arkenstone

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import akka.stream.ActorMaterializer
import com.oneapm.arkenstone.actors.DbActor

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by zhxiaog on 16/3/7.
  */
object Main
{

  implicit lazy val system       = ActorSystem("arkenstone");
  implicit lazy val materializer = ActorMaterializer()

  def main(args: Array[String]) {
    lazy val actor = system.actorOf(DbActor.props)

    val bindingFuture = Http().bindAndHandle(QueryAPI(actor).routes ~ IngestionAPI(actor).routes, "0.0.0.0", 8081)

    println(s"Server online at http://0.0.0.0:8081/\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ ⇒ system.terminate())
  }
}
