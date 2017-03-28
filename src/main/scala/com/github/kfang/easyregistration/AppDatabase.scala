package com.github.kfang.easyregistration

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.core.nodeset.Authenticate

import scala.concurrent.Future

class AppDatabase(driver: MongoDriver, conn: MongoConnection, db: DefaultDB) {

  val Registrants: BSONCollection = db[BSONCollection]("registrants")
  val Users: BSONCollection       = db[BSONCollection]("users")

}

object AppDatabase {

  def apply(implicit config: AppConfig): Future[AppDatabase] = {
    val driver = new MongoDriver()
    import driver.system.dispatcher
    val authentications = Seq(Authenticate(
      db = config.MONGO_AUTH_DB,
      user = config.MONGO_AUTH_USER,
      password = config.MONGO_AUTH_PASS
    ))
    val connection = driver.connection(config.MONGO_NODES, authentications = authentications)
    val database = connection.database(config.MONGO_DB)

    database.map(db => {
      new AppDatabase(driver, connection, db)
    })
  }

}


