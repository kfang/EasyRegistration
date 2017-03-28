package com.github.kfang.easyregistration.routing

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kfang.easyregistration.AppPackage

class V1Routes(App: AppPackage) {

  val routes: Route = get {
    complete("Hello World!")
  }

}
