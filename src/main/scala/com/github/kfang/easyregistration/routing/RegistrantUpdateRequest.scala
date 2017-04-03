package com.github.kfang.easyregistration.routing

import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models.RegistrantFlag
import reactivemongo.bson.{BSONDateTime, BSONDocument}
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import com.github.kfang.easyregistration.utils.MongoPartialUpdate
import spray.json.{JsObject, RootJsonFormat}

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

    def getResponse: Future[JsObject] = {

      Seq(
        MongoPartialUpdate("firstName", request.firstName, unsetOnEmpty = false),
        MongoPartialUpdate("lastName", request.lastName, unsetOnEmpty = false),
        MongoPartialUpdate("gender", request.lastName),
        MongoPartialUpdate("birthday", request.birthday),

        MongoPartialUpdate("allergies", request.allergies),
        MongoPartialUpdate("comments", request.comments),
        MongoPartialUpdate("extraInformation", request.extraInformation),

        MongoPartialUpdate("contacts", request.contacts),
        MongoPartialUpdate("flags", request.flags)
      ).flatten

      Future.successful(JsObject())
    }
  }

}
