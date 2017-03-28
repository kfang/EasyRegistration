package com.github.kfang.easyregistration.models

import java.util.UUID

import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}

case class Registrant (
  firstName: String,
  lastName: String,
  birthday: BSONDateTime,
  gender: String,

  allergies: Option[String],
  comments: Option[String],

  flags: Seq[String],     //TODO: enumerate different flags
  guardian: Option[String], //TODO: implement Guardian model
  extraInformation: Option[BSONDocument] = None,
  _id: String = UUID.randomUUID().toString
)

object Registrant {

  implicit val __bsf: BSONDocumentReader[Registrant]
    with BSONDocumentWriter[Registrant]
    with BSONHandler[BSONDocument, Registrant] = Macros.handler[Registrant]

}
