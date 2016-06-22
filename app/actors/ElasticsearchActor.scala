package actors


import akka.actor.{Actor, Props}
import model.WebRequestLog
import service.ElasticsearchService

import java.util.UUID

/**
 * Created by benjarman on 4/28/16.
 */
class ElasticsearchActor extends Actor{
  
  def receive = {
    case w: WebRequestLog => {
      val actorUUID = UUID.randomUUID().toString
      val child = context.actorOf(Props.empty, "child"+actorUUID)
      child ! w
      //System.out.println("actor ID: ----> \u001B[33m" + actorUUID + "\u001B[0m")
      ElasticsearchService.save[WebRequestLog, String](w, "test_index", "test_type")
    }
    case default => {
      throw new RuntimeException(s"invalid message sent to ElasticsearchActor: $default")
    }
  }
  
}