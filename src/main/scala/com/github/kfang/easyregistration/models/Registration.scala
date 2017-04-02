package com.github.kfang.easyregistration.models

import java.util.UUID

import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import spray.json.RootJsonFormat

import scala.util.Random

case class Registration(
  registrants: Seq[String],
  contacts: Seq[String],
  createdOn: BSONDateTime = BSONDateTime(System.currentTimeMillis()),
  code: String = Random.alphanumeric.take(8).mkString,
  _id: String = UUID.randomUUID().toString
)

object Registration {
  implicit val __bsf: BSONDocumentReader[Registration]
    with BSONDocumentWriter[Registration]
    with BSONHandler[BSONDocument, Registration] = Macros.handler[Registration]
  implicit val __jsf: RootJsonFormat[Registration] = jsonFormat5(Registration.apply)
}
