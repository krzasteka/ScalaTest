package actors


import akka.actor.Actor
import model.WebRequestLog
import service.ElasticsearchService

/**
 * Created by benjarman on 4/28/16.
 */
class ElasticsearchActor extends Actor{
  def receive = {
    case w: WebRequestLog => {
      ElasticsearchService.save[WebRequestLog, String](w, "test_index", "test_type")
    }
    case default => {
      throw new RuntimeException(s"invalid message sent to ElasticsearchActor: $default")
    }
  }
}