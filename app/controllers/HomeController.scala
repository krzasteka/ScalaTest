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
import play.api.cache._

import akka.actor.{ActorSystem, ActorRef, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.routing.RoundRobinPool

import concurrent.Future
import concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeController @Inject()(ws: WSClient, system: ActorSystem, configuration: play.api.Configuration, cache: CacheApi) extends Controller {
  
  val numOfActors = configuration.underlying.getString("routerConfig.numOfActors").toInt 
  val cacheDuration = configuration.underlying.getString("cache.duration").toInt
  implicit val actorSystem = ActorSystem("coding-challenge-system")
  val esActorsRouter = actorSystem.actorOf(RoundRobinPool(numOfActors).props(Props[ElasticsearchActor]), "esRouter")
  val kActorsRouter = actorSystem.actorOf(RoundRobinPool(numOfActors).props(Props[KafkaActor]), "kRouter")
      
  def getRequest(path: String) = Action.async {
    request => { 
        val reqUrl = "http://demo.ckan.org/api/3" + request.uri.toString
        val requestor = WebRequest(reqUrl, request.body.toString, 0)
        
        request.method match {
          case "GET" =>{
            cachedOrStore(request, reqUrl, requestor)
          }
          
          case "PUT" =>{
            cachedOrStore(request, reqUrl, requestor) 
          }
          
          case "POST" =>{
            cachedOrStore(request, reqUrl, requestor) 
          }
          
          case "DELETE" =>{
            cachedOrStore(request, reqUrl, requestor) 
          }
          
          case _ => Future {
            Ok("oops invalid request method: " + request.method) 
          }
      }
    }
  }
  
  def cachedOrStore(request: Request[AnyContent], reqUrl: String, requestor: WebRequest): Future[Result] = {
    val cached = cache.get[Any](request.method+reqUrl)
    cached match {
      case Some(cachedThing) => Future { Ok(cachedThing.toString) } 
      case None => {
        ws.url(reqUrl).execute(request.method).map { response =>
          val wsResponse = WebRequest(reqUrl, response.body, response.status)
          val wrl  = WebRequestLog(requestor, wsResponse)
          esActorsRouter ! wrl
          kActorsRouter ! wrl
          cache.set(request.method+reqUrl,response.body, cacheDuration.minutes)
          Ok("Status: " + response.status.toString)
        }
      } 
    }
  }

}
