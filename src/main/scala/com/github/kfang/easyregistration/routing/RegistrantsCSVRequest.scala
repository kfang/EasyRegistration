package com.github.kfang.easyregistration.routing

import java.util.Date

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models._
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

class RegistrantsCSVRequest(implicit App: AppPackage) {
  import App.sys.dispatcher

  private def printContact(contact: Contact, extras: Seq[String]): Seq[String] = {
    val home = contact.extraInformation.flatMap(_.getAs[String]("homeChurch")).getOrElse("")
    Seq(
      contact.firstName,
      contact.lastName,

      contact.street.getOrElse(""),
      contact.city.getOrElse(""),
      contact.state.getOrElse(""),
      contact.zip.getOrElse(""),

      contact.email.getOrElse(""),
      contact.homePhone.getOrElse(""),
      contact.cellPhone.getOrElse(""),
      contact.relationship.getOrElse("")
    ).:+(home).map(_.trim).map(s => "\"" + s + "\"")
  }

  private def printRegistrant(registrant: Registrant): Seq[String] = {
    val doc = registrant.extraInformation.getOrElse(BSONDocument.empty)
    Seq(
      "\"" + registrant.firstName + "\"",
      "\"" + registrant.lastName + "\"",
      "\"" + registrant.gender + "\"",
      "\"" + new Date(registrant.birthday.value).toString + "\"",
      "\"" + registrant.allergies.getOrElse("") + "\"",
      "\"" + registrant.comments.getOrElse("") + "\"",
      registrant.flags.getOrElse(Nil).headOption.getOrElse(RegistrantFlag.Unpaid).entryName,
      doc.getAs[String]("grade").getOrElse(""),
      doc.getAs[String]("stay").getOrElse(""),
      doc.getAs[String]("tShirt").getOrElse("")
    ).map(_.trim)
  }

  private def printRegistrant(registrant: Registrant, registration: Registration, contacts: Seq[Contact]): String = {
    val emergency = contacts.find(_.flags.exists(_.contains(ContactFlag.Emergency)))
    val guardian = contacts.find(_.flags.exists(_.contains(ContactFlag.Guardian)))
    val extras = contacts.flatMap(_.extraInformation).flatMap(_.toMap.keys)
    lazy val empty = (1 to (10 + extras.size)).map(_ => "")

    Seq(
      printRegistrant(registrant),
      guardian.map(printContact(_, extras)).getOrElse(empty),
      emergency.map(printContact(_, extras)).getOrElse(empty)
    ).flatten.mkString(",")
  }

  private def printRegistrations(registration: Registration): Future[Seq[String]] = {
    for {
      contacts <- App.db.Contacts
        .find(BSONDocument("_id" -> BSONDocument("$in" -> registration.contacts)))
        .cursor[Contact]().collect[Seq](maxDocs = registration.contacts.size, Cursor.FailOnError[Seq[Contact]]())
      registrants <- App.db.Registrants
        .find(BSONDocument("_id" -> BSONDocument("$in" -> registration.registrants)))
        .cursor[Registrant]().collect[Seq](maxDocs = registration.registrants.size, Cursor.FailOnError[Seq[Registrant]]())
    } yield {
      registrants.map(r => printRegistrant(r, registration, contacts))
    }
  }

  private def getLines: Future[String] = {
    for {
      registrations <- App.db.Registrations.find(BSONDocument())
        .cursor[Registration]()
        .collect[Seq](-1, Cursor.FailOnError[Seq[Registration]]())
      lines <- Future.sequence(registrations.map(printRegistrations))
    } yield {
      val header = Seq(
        "First Name",
        "Last Name",
        "Gender",
        "Birthday",
        "Allergies",
        "Comments",
        "Paid",
        "Grade",
        "Stay",
        "Shirt",
        "Guardian First",
        "Guardian Last",
        "Guardian Street",
        "Guardian City",
        "Guardian State",
        "Guardian Zip",
        "Guardian Email",
        "Guardian Home",
        "Guardian Cell",
        "Guardian Relationship",
        "Guardian Home Church",
        "Emergency First",
        "Emergency Last",
        "Emergency Street",
        "Emergency City",
        "Emergency State",
        "Emergency Zip",
        "Emergency Email",
        "Emergency Home",
        "Emergency Cell",
        "Emergency Relationship",
        "Emergency Home Church"
      ).mkString(",")

      lines.flatten.+:(header).mkString("\n")
    }
  }

  def getResponse: Route = {
    onSuccess(getLines){
      (lines) => {
        complete(HttpEntity(ContentTypes.`text/csv(UTF-8)`, lines))
      }
    }
  }
}
