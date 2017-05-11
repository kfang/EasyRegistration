package com.github.kfang.easyregistration.routing

import java.util.UUID

import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models.{ApiError, Registrant, RegistrantFlag}
import reactivemongo.bson.{BSONDateTime, BSONDocument}
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import com.github.kfang.easyregistration.utils.MongoPartialUpdate
import spray.json._

import scala.concurrent.Future

case class RegistrantUpdateRequest(
  firstName: Option[String],
  lastName: Option[String],
  gender: Option[String],
  birthday: Option[BSONDateTime],

  allergies: Option[String],
  comments: Option[String],
  extraInformation: Option[BSONDocument],

  contacts: Option[Seq[String]],
  flags: Option[Seq[RegistrantFlag]]
)

object RegistrantUpdateRequest {

  implicit val __jsf: RootJsonFormat[RegistrantUpdateRequest] = jsonFormat9(RegistrantUpdateRequest.apply)

  implicit class RegistrantUpdate(request: RegistrantUpdateRequest)(implicit App: AppPackage){
    import App.sys.dispatcher
    private implicit val __db = App.db

    private def updateRegistrant(r: Registrant, update: Option[BSONDocument]): Future[Registrant] = {
      val _update = for {
        id  <- r._id
        upd <- update
        sel =  BSONDocument("_id" -> id)
      } yield {
        App.db.Registrants.findAndUpdate(
          selector = sel,
          update = upd,
          fetchNewObject = true
        ).map(_.result[Registrant])
      }

      _update.getOrElse(Future.successful(None)).map(_.getOrElse(r))
    }

    def getResponse(id: UUID): Future[JsObject] = {

      for {
        registrant <- Registrant.findById(id.toString).map({
          case None    => throw ApiError.notFound("registrant-not-found" -> JsString("Registrant was not found"))
          case Some(r) => r
        })

        update = Seq(
          MongoPartialUpdate("firstName", request.firstName, unsetOnEmpty = false),
          MongoPartialUpdate("lastName", request.lastName, unsetOnEmpty = false),
          MongoPartialUpdate("gender", request.lastName),
          MongoPartialUpdate("birthday", request.birthday),

          MongoPartialUpdate("allergies", request.allergies),
          MongoPartialUpdate("comments", request.comments),
          MongoPartialUpdate("extraInformation", request.extraInformation),

          MongoPartialUpdate("contacts", request.contacts),
          MongoPartialUpdate("flags", request.flags)
        ).flatten.toUpdate

        updatedRegistrant <- updateRegistrant(registrant, update)
      } yield {
        JsObject(
          "registrants" -> Seq(updatedRegistrant).toJson
        )
      }
    }
  }

}
