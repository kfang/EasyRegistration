package com.github.kfang.easyregistration.models

import java.util.UUID

import com.github.kfang.easyregistration.AppDatabase
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONHandler, Macros}
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContext, Future}

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
  createdOn: Option[BSONDateTime] = Some(BSONDateTime(System.currentTimeMillis())),
  _id: Option[String] = Some(UUID.randomUUID().toString)
)

object Registrant {

  implicit val __bsf: BSONDocumentReader[Registrant]
    with BSONDocumentWriter[Registrant]
    with BSONHandler[BSONDocument, Registrant] = Macros.handler[Registrant]
  implicit val __jsf: RootJsonFormat[Registrant] = jsonFormat11(Registrant.apply)

  val MONGO_INDEXES: Seq[Index] = Seq(
    Index(key = Seq("firstName" -> IndexType.Text, "lastName" -> IndexType.Text)),
    Index(key = Seq("createdOn" -> IndexType.Descending)),
    Index(key = Seq("flags" -> IndexType.Ascending))
  )

  implicit class RegistrantUtils(t: Registrant.type)(implicit db: AppDatabase, ctx: ExecutionContext){
    def findById(id: String): Future[Option[Registrant]] = {
      db.Registrants.find(BSONDocument("_id" -> id)).one[Registrant]
    }
  }

}
