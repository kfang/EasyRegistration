package com.github.kfang.easyregistration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.kfang.easyregistration.routing.V1Routes
import reactivemongo.api.MongoDriver

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  private val appConfig = new AppConfig
  appConfig.printConfig()

  private implicit val system           = ActorSystem(appConfig.SYSTEM_NAME)
  private implicit val materializer     = ActorMaterializer()
  private implicit val executionContext = system.dispatcher
  private implicit val driver           = MongoDriver()

  for {
    appDB   <- AppDatabase(appConfig, driver)
    srvs    =  AppServices(appConfig)
    appPkg  =  AppPackage(system, appConfig, appDB, srvs)
    routing =  new V1Routes()(appPkg).routes
    bind    <- Http().bindAndHandle(routing, interface = appConfig.HTTP_INTERFACE, port = appConfig.HTTP_PORT)
  } yield {
    sys.addShutdownHook({
      val task = for {
        _ <- bind.unbind()
        _ =  driver.close()
        _ <- system.terminate()
      } yield {}

      Await.ready(task, 5.minutes)
    })
  }

}
