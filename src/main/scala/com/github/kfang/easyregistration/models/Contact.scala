package com.github.kfang.easyregistration.models

import java.util.UUID

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import spray.json.RootJsonFormat
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._

case class Contact(
  firstName: String,
  lastName: String,

  street: Option[String],
  city: Option[String],
  state: Option[String],
  zip: Option[String],

  homePhone: Option[String],
  cellPhone: Option[String],
  relationship: Option[String],

  flags: Option[Seq[ContactFlag]],
  extraInformation: Option[BSONDocument],
  _id: String = UUID.randomUUID().toString
)

object Contact {
  implicit val __bsf: BSONDocumentReader[Contact]
    with BSONDocumentWriter[Contact]
    with BSONHandler[BSONDocument, Contact] = Macros.handler[Contact]
  implicit val __jsf: RootJsonFormat[Contact] = jsonFormat12(Contact.apply)
}
