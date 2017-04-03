package com.github.kfang.easyregistration.routing

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.directives.ParameterDirectives._
import com.github.kfang.easyregistration.AppPackage
import com.github.kfang.easyregistration.models.Registrant
import com.github.kfang.easyregistration.utils.{StringUnmarshallers, UnmarshalledSort}
import reactivemongo.api.{Cursor, QueryOpts}
import reactivemongo.bson.BSONDocument
import com.github.kfang.easyregistration.utils.BsonJsonProtocol._
import spray.json._

import scala.concurrent.Future

case class RegistrantListRequest(
  page: Int,
  limit: Int,
  sideload: Option[Seq[String]],
  sort: Option[UnmarshalledSort]
)

object RegistrantListRequest {

  val params: Directive1[RegistrantListRequest] = parameters(
    'page.?(0),
    'limit.?(30),
    'sideload.as(StringUnmarshallers.StringSeq).?,
    'sort.as(UnmarshalledSort).?
  ).as(RegistrantListRequest.apply _)


  implicit class RegistranatList(request: RegistrantListRequest)(implicit App: AppPackage){
    import App.sys.dispatcher

    private val limit = math.max(request.limit, 0)
    private val skipN = math.max(request.page, 0) * limit
    private val sortD = request.sort.map(_.asBSONSort).getOrElse(BSONDocument("createdOn" -> -1))

    private val selector = BSONDocument()

    private def getRegistrants: Future[Seq[Registrant]] = {
      App.db.Registrants.find(selector)
        .options(QueryOpts(skipN = skipN))
        .sort(sortD).cursor[Registrant]()
        .collect[Seq](maxDocs = limit, err = Cursor.FailOnError[Seq[Registrant]]())
    }

    private def getNumFound: Future[Int] = {
      App.db.Registrants.count(Some(selector))
    }

    def getResponse: Future[JsObject] = {
      for {
        registranats  <- getRegistrants
        numFound      <- getNumFound
      } yield {
        JsObject(
          "registrants" -> registranats.toJson,
          "numFound" -> JsNumber(numFound)
        )
      }
    }
  }
}
