package com.github.kfang.easyregistration.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class V1Routes {

  val routes: Route = get {
    complete("Hello World!")
  }

}
