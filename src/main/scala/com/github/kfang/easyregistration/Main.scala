package com.github.kfang.easyregistration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.kfang.easyregistration.routing.V1Routes

object Main extends App {

  private val appConfig = new AppConfig
  appConfig.printConfig()

  private implicit val system           = ActorSystem(appConfig.SYSTEM_NAME)
  private implicit val materializer     = ActorMaterializer()
  private implicit val executionContext = system.dispatcher

  for {
    appDB   <- AppDatabase(appConfig)
    appPkg  =  AppPackage(system, appConfig, appDB)
    routing =  new V1Routes()(appPkg).routes
    _       <- Http().bindAndHandle(routing, interface = appConfig.HTTP_INTERFACE, port = appConfig.HTTP_PORT)
  } yield {
  }

}
