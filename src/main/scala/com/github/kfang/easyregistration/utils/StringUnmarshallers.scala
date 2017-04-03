package com.github.kfang.easyregistration.utils

import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.Materializer

import scala.concurrent.{ExecutionContext, Future}

trait StringUnmarshallers {

  case object StringSeq extends Unmarshaller[String, Seq[String]] {
    override def apply(value: String)(implicit ec: ExecutionContext, materializer: Materializer): Future[Seq[String]] = {
      Future.successful(value.split(',').toSeq)
    }
  }

}

object StringUnmarshallers extends StringUnmarshallers
