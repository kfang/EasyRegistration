package com.github.kfang.easyregistration

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.kfang.easyregistration.routing.V1Routes
import reactivemongo.api.MongoDriver

object Main extends App {

  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()

  private implicit val executionContext = system.dispatcher

  private val driver = new MongoDriver()
  private val connection = driver.connection(Seq("localhost"))
  private val database = connection.database("local")
  database.flatMap(_.collectionNames).map(_.foreach(println))

  private val routing = new V1Routes().routes
  Http().bindAndHandle(routing, "0.0.0.0", 8080)
}
