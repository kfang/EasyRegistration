package com.github.kfang.easyregistration.models

import java.util.UUID

import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import spray.json.RootJsonFormat

case class Registrant (
  firstName: String,
  lastName: String,
  birthday: BSONDateTime,
  gender: String,

  allergies: Option[String],
  comments: Option[String],

  flags: Seq[String],     //TODO: enumerate different flags
  guardian: Option[String],
  extraInformation: Option[BSONDocument] = None,
  _id: String = UUID.randomUUID().toString
)

object Registrant {
  implicit val __bsf: BSONDocumentReader[Registrant]
    with BSONDocumentWriter[Registrant]
    with BSONHandler[BSONDocument, Registrant] = Macros.handler[Registrant]
  implicit val __jsf: RootJsonFormat[Registrant] = jsonFormat10(Registrant.apply)
}
