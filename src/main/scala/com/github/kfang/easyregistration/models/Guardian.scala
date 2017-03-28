package com.github.kfang.easyregistration.models

import java.util.UUID

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}

case class Guardian(
  firstName: String,
  lastName: String,
  email: String,

  street: String,
  city: String,
  state: String,
  zip: String,

  homePhone: String,
  cellPhone: String,

  extraInformation: Option[BSONDocument] = None,

  _id: String = UUID.randomUUID().toString
)

object Guardian {
  implicit val __bsf: BSONDocumentReader[Guardian]
    with BSONDocumentWriter[Guardian]
    with BSONHandler[BSONDocument, Guardian] = Macros.handler[Guardian]
}
