import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.{StreamingContext, _}

/**
 * Created by benjarman on 4/27/16.
 */
object KafkaConsumer extends App {

  val topics = Array("hart_test")
  val zkQuorum = "localhost"

  val sparkConf = new SparkConf().setAppName("Consumer").setMaster("local[4]")
  val ssc = new StreamingContext(sparkConf, Seconds(5))

  ssc.checkpoint("checkpoint")

  val topicMap = topics.map((_, 4)).toMap
  val kafkaStream = KafkaUtils.createStream(
    ssc, zkQuorum, "Consumer", topicMap
  ).map(_._2)

  //kafkaStream.print()

  kafkaStream.foreachRDD(rdd => {
    rdd.saveAsTextFile("/tmp/consumer_output")
  })

  ssc.start()
  ssc.awaitTermination()

}