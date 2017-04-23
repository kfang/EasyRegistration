package com.github.kfang.easyregistration.routing

import java.util.UUID

import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models.{Contact, Registrant, Registration}
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONDocument
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.Future

class RegistrantReadRequest(implicit App: AppPackage){
  import App.sys.dispatcher

  def getResponse(id: UUID): Future[JsObject] = {
    for {
      registrant   <- App.db.Registrants.find(BSONDocument("_id" -> id.toString)).one[Registrant]
      registration <- App.db.Registrations.find(BSONDocument("registrants" -> id.toString)).one[Registration]
      contacts     <- registration.map(registration => {
        App.db.Contacts.find(BSONDocument("_id" -> BSONDocument("$in" -> registration.contacts)))
          .cursor[Contact]().collect[Seq](maxDocs = registration.contacts.size, err = Cursor.FailOnError[Seq[Contact]]())
      }).getOrElse(Future.successful(Nil))
    } yield {
      JsObject(
        "registrants" -> List(registrant).flatten.toJson,
        "registrations" -> List(registration).flatten.toJson,
        "contacts" -> contacts.toJson
      )
    }
  }
}
