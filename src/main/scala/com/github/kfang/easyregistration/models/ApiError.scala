package com.github.kfang.easyregistration.models

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import spray.json.{JsObject, JsString}

case class ApiError(
  code: StatusCode,
  body: JsObject
) extends Throwable

object ApiError {

  def notFound(js: (String, JsString)): ApiError = {
    ApiError(code = StatusCodes.NotFound, body = JsObject("errors" -> JsObject(js)))
  }

  def badRequest(js: (String, JsString)): ApiError = {
    ApiError(code = StatusCodes.BadRequest, body = JsObject("errors" -> JsObject(js)))
  }

}
