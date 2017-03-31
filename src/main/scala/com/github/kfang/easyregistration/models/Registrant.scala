package com.github.kfang.easyregistration.models

import java.util.UUID

import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import spray.json.RootJsonFormat

case class Registrant (
  firstName: String,
  lastName: String,
  gender: String,
  birthday: BSONDateTime,

  allergies: Option[String],
  comments: Option[String],
  extraInformation: Option[BSONDocument] = None,

  contacts: Option[Seq[String]],
  flags: Option[Seq[RegistrantFlag]],
  createdOn: BSONDateTime = BSONDateTime(System.currentTimeMillis()),
  _id: String = UUID.randomUUID().toString
)

object Registrant {
  implicit val __bsf: BSONDocumentReader[Registrant]
    with BSONDocumentWriter[Registrant]
    with BSONHandler[BSONDocument, Registrant] = Macros.handler[Registrant]
  implicit val __jsf: RootJsonFormat[Registrant] = jsonFormat11(Registrant.apply)
}
