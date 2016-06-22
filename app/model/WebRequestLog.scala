package model

import java.util.UUID
/**
 * Created by benjarman on 4/27/16.
 */
case class WebRequest(url: String, body: String, status: Int)

case class WebRequestLog(
                        requestor: WebRequest,
                        response: WebRequest,
                        _id: String = UUID.randomUUID().toString
                          ) extends IDTrait[String]{
}