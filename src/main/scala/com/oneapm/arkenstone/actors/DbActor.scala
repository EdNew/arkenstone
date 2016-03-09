package com.oneapm.arkenstone.actors

import akka.actor.{Props, Actor}

object DbActor
{
  val props: Props = Props(classOf[DbActor])

  case class Row(key: String)

  case class Query(key: String)

}

/**
  * Created by zhxiaog on 16/3/8.
  */
class DbActor extends Actor
{

  import DbActor._

  var map = Map[String, Long]()
  var idx = 0

  override def receive = {
    case Row(k) => {
      idx += 1
      map = map + (k -> idx)
    }
    case Query(k) => {
      sender ! map(k)
    }
  }
}
