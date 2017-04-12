package com.github.kfang.easyregistration.routing

import java.util.UUID

import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models._
import com.github.kfang.easyregistration.services.EmailService.Email
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import reactivemongo.bson.BSONDateTime
import spray.json._

import scala.concurrent.Future

case class RegistrationCreateRequest(
  registrants: Seq[Registrant],
  contacts: Seq[Contact]
)

object RegistrationCreateRequest {
  implicit val __jsf: RootJsonFormat[RegistrationCreateRequest] = jsonFormat2(RegistrationCreateRequest.apply)

  implicit class RegistrationCreate(request: RegistrationCreateRequest)(implicit App: AppPackage){
    import App.sys.dispatcher

    private def sendEmail(registration: Registration, registrants: Seq[Registrant]) = {
      for {
        recipient <- request.contacts.find(_.flags.exists(_.contains(ContactFlag.Guardian))).flatMap(_.email)
        subject = "Thanks for Registering!"
        message =
          s"""
            |Thanks for registering!
            |
            |Your confirmation code: ${registration.code}
            |
            |We received the following registrants:
            |${registrants.map(r => s"${r.firstName} ${r.lastName}").mkString("\n")}
          """.stripMargin
      } yield {
        App.services.emailService ! Email(recipient, subject, message)
      }
    }

    def getResponse: Future[JsObject] = {

      val currentTime = BSONDateTime(System.currentTimeMillis())

      val contacts = request.contacts.map(_.copy(
        createdOn = Some(currentTime),
        _id = Some(UUID.randomUUID().toString)
      ))

      val registrants = request.registrants.map(_.copy(
        flags = Some(Seq(RegistrantFlag.Unpaid)),
        createdOn = Some(currentTime),
        contacts = Some(contacts.flatMap(_._id)),
        _id = Some(UUID.randomUUID().toString)
      ))

      val registration = Registration(
        registrants = registrants.map(_._id.get),
        contacts = contacts.map(_._id.get),
        createdOn = currentTime
      )

      for {
        _ <- Future.sequence(contacts.map(App.db.Contacts.insert(_)))
        _ <- Future.sequence(registrants.map(App.db.Registrants.insert(_)))
        _ <- App.db.Registrations.insert(registration)
        _ =  sendEmail(registration, registrants)
      } yield {
        JsObject(
          "registration" -> registration.toJson,
          "contacts" -> contacts.toJson,
          "registrants" -> registrants.toJson
        )
      }
    }
  }
}
