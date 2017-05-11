package com.github.kfang.easyregistration.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kfang.easyregistration.AppPackage
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.Credentials
import spray.json.JsObject

import scala.concurrent.Future
import ch.megard.akka.http.cors.CorsDirectives._

import scala.util.{Failure, Success}

class V1Routes(implicit App: AppPackage) {

  implicit def fToR(f: Future[JsObject]): Route = {
    onComplete(f){
      case Success(jso) => complete(jso)
      case Failure(err) => complete(StatusCodes.InternalServerError -> err.toString)
    }
  }

  //TODO: actually implement auth
  private def upass(credentials: Credentials): Option[String] = {
    val pass = App.config.SYSTEM_CREDENTIALS_PASSWORD
    credentials match {
      case c@Credentials.Provided(u) if c.verify(pass) => Some(u)
      case _ => None
    }
  }
  private val basicAuth = authenticateBasic("", upass)

  val routes: Route = cors(){
    pathPrefix("registrants"){
      (get & pathEnd & RegistrantListRequest.params & basicAuth){
        (request, _) => request.getResponse
      } ~
      (get & path(JavaUUID) & basicAuth){
        (id, _) => new RegistrantReadRequest().getResponse(id)
      } ~
      (put & path(JavaUUID) & entity(as[RegistrantUpdateRequest]) &basicAuth){
        (id, request, _) => request.getResponse(id)
      }
    } ~
    pathPrefix("registrations"){
      (post & pathEnd & entity(as[RegistrationCreateRequest])){
        (request) => request.getResponse
      }
    }
  }

}
