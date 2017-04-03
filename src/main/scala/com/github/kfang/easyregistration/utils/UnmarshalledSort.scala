package com.github.kfang.easyregistration.utils

import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.Materializer
import reactivemongo.bson.{BSONDocument, BSONInteger}

import scala.concurrent.{ExecutionContext, Future}

case class UnmarshalledSort(sorts: Seq[(String, String)]){
  def asBSONSort: BSONDocument = {
    BSONDocument(sorts.map({
      case (field, "asc")  => field -> BSONInteger(1)
      case (field, "desc") => field -> BSONInteger(-1)
    }))
  }
}

object UnmarshalledSort extends Unmarshaller[String, UnmarshalledSort]{

  override def apply(value: String)(implicit ec: ExecutionContext, materializer: Materializer): Future[UnmarshalledSort] = {
    Future.successful({

      value.split(',').toSeq.flatMap(_.split(':').toList match {
        case field :: "asc" :: Nil => Some((field, "asc"))
        case field :: "desc" :: Nil => Some((field, "desc"))
        case _=> None
      })

    }).map(sorts => {

      UnmarshalledSort(sorts)

    })
  }

}
