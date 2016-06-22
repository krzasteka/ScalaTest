package controllers

import actors.ElasticsearchActor
import actors.KafkaActor

import model.WebRequest
import model.WebRequestLog

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.ws._
import play.api.http.HttpEntity

import akka.actor.{ActorSystem, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.routing.RoundRobinPool
import concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeController @Inject()(ws: WSClient, system: ActorSystem) extends Controller {
  
  implicit val actorSystem = ActorSystem("coding-challenge-system")
  val esActorsRouter = actorSystem.actorOf(RoundRobinPool(5).props(Props[ElasticsearchActor]), "esRouter")
  val kActorsRouter = actorSystem.actorOf(RoundRobinPool(5).props(Props[KafkaActor]), "kRouter")
      
  def getRequest(path: String) = Action.async {
    request => { 
        val reqUrl = "http://demo.ckan.org/api/3" + request.uri.toString
        val requestor = WebRequest(reqUrl, request.body.toString, 0)
        
        request.method match {
          case "GET" =>{
            ws.url(reqUrl).get().map { response =>
              val wsResponse = WebRequest(reqUrl, response.body, response.status)
              val wrl  = WebRequestLog(requestor, wsResponse)
              esActorsRouter ! wrl
              kActorsRouter ! wrl
              Ok("Status: " + response.status.toString)
            }  
          }
          
          case "PUT" =>{
            ws.url(reqUrl).execute("PUT").map { response =>
              val wsResponse = WebRequest(reqUrl, response.body, response.status)
              val wrl  = WebRequestLog(requestor, wsResponse)
              esActorsRouter ! wrl
              kActorsRouter ! wrl
              Ok("Status: " + response.status.toString)
            }  
          }
          
          case "POST" =>{
            ws.url(reqUrl).execute("POST").map { response =>
              val wsResponse = WebRequest(reqUrl, response.body, response.status)
              val wrl  = WebRequestLog(requestor, wsResponse)
              esActorsRouter ! wrl
              kActorsRouter ! wrl
              Ok("Status: " + response.status.toString)
            }  
          }
          
          case "DELETE" =>{
            ws.url(reqUrl).execute("DELETE").map { response =>
              val wsResponse = WebRequest(reqUrl, response.body, response.status)
              val wrl  = WebRequestLog(requestor, wsResponse)
              esActorsRouter ! wrl
              kActorsRouter ! wrl
              Ok("Status: " + response.status.toString)
            }  
          }
          
          case _ => Future {
                      Ok("oops invalid request method: " + request.method) 
                    }
      }
    }
  }

}
