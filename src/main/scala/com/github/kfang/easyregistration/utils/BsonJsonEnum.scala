package com.github.kfang.easyregistration.utils

import enumeratum._
import reactivemongo.bson.{BSONHandler, BSONString}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, RootJsonFormat}

trait BsonJsonEnum[T <: EnumEntry] {
  this: Enum[T] =>

  implicit val __jsf = new RootJsonFormat[T] {
    override def write(obj: T): JsValue = JsString(obj.entryName)
    override def read(json: JsValue): T = withName(json.convertTo[String])
  }

  implicit val __bsf = new BSONHandler[BSONString, T] {
    def read(bson: BSONString): T = withName(bson.value)
    def write(t: T): BSONString = BSONString(t.entryName)
  }

}
