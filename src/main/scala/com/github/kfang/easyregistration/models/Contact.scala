package com.github.kfang.easyregistration.models

import java.util.UUID

import com.github.kfang.easyregistration.AppDatabase
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import spray.json.RootJsonFormat
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import reactivemongo.api.Cursor

import scala.concurrent.{ExecutionContext, Future}

case class Contact(
  firstName: String,
  lastName: String,

  street: Option[String],
  city: Option[String],
  state: Option[String],
  zip: Option[String],

  email: Option[String],
  homePhone: Option[String],
  cellPhone: Option[String],
  relationship: Option[String],

  flags: Option[Seq[ContactFlag]],
  extraInformation: Option[BSONDocument],
  createdOn: Option[BSONDateTime] = Some(BSONDateTime(System.currentTimeMillis())),
  _id: Option[String] = Some(UUID.randomUUID().toString)
)

object Contact {

  implicit val __bsf: BSONDocumentReader[Contact]
    with BSONDocumentWriter[Contact]
    with BSONHandler[BSONDocument, Contact] = Macros.handler[Contact]
  implicit val __jsf: RootJsonFormat[Contact] = jsonFormat14(Contact.apply)

  implicit class ContactUtils(t: Contact.type)(implicit db: AppDatabase, ctx: ExecutionContext){

    def findByIds(ids: Seq[String]): Future[Seq[Contact]] = {
      db.Contacts.find(BSONDocument("_id" -> BSONDocument("$in" -> ids)))
        .cursor[Contact]().collect(maxDocs = -1, Cursor.FailOnError[Seq[Contact]]())
    }

  }
}
