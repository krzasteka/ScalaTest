package actors

import akka.actor.{Actor, Props}
import model.WebRequestLog
import model.KafkaMessage
import service.KafkaService

import java.util.UUID

/**
 * Created by benjarman on 4/28/16.
 */
class KafkaActor extends Actor{
  def receive = {
    case w: WebRequestLog => {
      
      val actorUUID = UUID.randomUUID().toString
      val child = context.actorOf(Props.empty, "child"+actorUUID)
      child ! w
      
      /* Add code here to take a WebRequestLog and convert it to a Kafka message.
       * Then use the KafkaService to send the message to kafka */
      
      val kafkaMsg = KafkaMessage(w.toString())
      KafkaService.apply(kafkaMsg)
    }
    case default => {
      throw new RuntimeException(s"invalid message sent to KafkaActor: $default")
    }
  }
}