package com.github.kfang.easyregistration.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kfang.easyregistration.AppPackage
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.JsObject

import scala.concurrent.Future
import ch.megard.akka.http.cors.CorsDirectives._

class V1Routes(implicit App: AppPackage) {

  implicit def fToR(f: Future[JsObject]): Route = {
    onSuccess(f)(js => complete(js))
  }

  val routes: Route = cors(){
    pathPrefix("registrations"){
      (post & pathEnd & entity(as[RegistrationCreateRequest])){
        (request) => request.getResponse
      }
    } ~
    complete("Hello World!")
  }

}
