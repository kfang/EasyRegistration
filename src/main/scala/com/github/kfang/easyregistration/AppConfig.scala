package com.github.kfang.easyregistration

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class AppConfig {

  val CONFIG: Config = ConfigFactory.load.getConfig("easy-registration")

  val HTTP_INTERFACE: String = CONFIG.getString("http.interface")
  val HTTP_PORT: Int         = CONFIG.getInt("http.port")

  val MONGO_AUTH_DB: String     = CONFIG.getString("mongo.auth.db")
  val MONGO_AUTH_USER: String   = CONFIG.getString("mongo.auth.username")
  val MONGO_AUTH_PASS: String   = CONFIG.getString("mongo.auth.password")
  val MONGO_NODES: Seq[String]  = CONFIG.getStringList("mongo.nodes").asScala
  val MONGO_DB: String          = CONFIG.getString("mongo.db")

  val SYSTEM_NAME: String = CONFIG.getString("system.name")


  def printConfig(): Unit = {
    LoggerFactory.getLogger(getClass).info(
      s"""
         |********************************************************************************
         |HTTP:
         |  INTERFACE: $HTTP_INTERFACE
         |  PORT: $HTTP_PORT
         |MONGO:
         |  NODES: ${MONGO_NODES.mkString(", ")}
         |  DB: $MONGO_DB
         |SYSTEM NAME: $SYSTEM_NAME
         |********************************************************************************
    """.stripMargin)
  }
}
