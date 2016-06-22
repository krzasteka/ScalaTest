package service

import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import model.KafkaMessage

import scala.util.Random

/**
 * Created by benjarman on 4/28/16.
 */

object KafkaService {

  val props = new Properties()
  props.put("metadata.broker.list", "localhost:9092")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("partitioner.class", "kafka.producer.DefaultPartitioner")
  props.put("request.required.acks", "1")
  props.put("producer.type", "sync")

  val config = new ProducerConfig(props)
  
  val producer = new Producer[String, String](config)
  
  def apply(msg: KafkaMessage) {
    val data = new KeyedMessage[String, String](msg.topic, msg.hashKey, msg.message)
    producer.send(data)
  }
}