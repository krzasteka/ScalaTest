
package model

import scala.util.Random

/**
 * Created by benjarman on 4/28/16.
 */
case class KafkaMessage(message: String, topic: String="hart_test", hashKey: String=new Random().nextString(5))