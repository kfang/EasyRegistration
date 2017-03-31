package com.github.kfang.easyregistration.utils

import reactivemongo.bson.{BSONArray, BSONBoolean, BSONDateTime, BSONDocument, BSONDouble, BSONInteger, BSONLong, BSONNull, BSONString, BSONValue}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

trait BsonJsonProtocol extends DefaultJsonProtocol {

  private implicit val bvalJS: RootJsonFormat[BSONValue] = new RootJsonFormat[BSONValue] {
    override def read(json: JsValue): BSONValue = json match {
      case js: JsString   => BSONString(js.value)
      case js: JsBoolean  => BSONBoolean(js.value)
      case js: JsNumber   => BSONDouble(js.value.doubleValue())
      case js: JsArray    => barrJS.read(js)
      case js: JsObject   => bdocJS.read(js)
      case JsNull         => BSONNull
      case js             => throw DeserializationException(s"unable to read $js to bson")
    }

    override def write(obj: BSONValue): JsValue = obj match {
      case bs: BSONString   => JsString(bs.value)
      case bs: BSONBoolean  => JsBoolean(bs.value)
      case bs: BSONInteger  => JsNumber(bs.value)
      case bs: BSONLong     => JsNumber(bs.value)
      case bs: BSONDouble   => JsNumber(bs.value)
      case bs: BSONArray    => barrJS.write(bs)
      case bs: BSONDocument => bdocJS.write(bs)
      case bs: BSONDateTime => bdatJS.write(bs)
      case BSONNull         => JsNull
      case bs               => throw DeserializationException(s"unable to write $bs to json")
    }
  }

  implicit val bdatJS = new RootJsonFormat[BSONDateTime] {
    override def read(json: JsValue): BSONDateTime = BSONDateTime(json.convertTo[Long])
    override def write(obj: BSONDateTime): JsValue = JsNumber(obj.value)
  }

  implicit val barrJS: RootJsonFormat[BSONArray] = new RootJsonFormat[BSONArray] {
    override def read(json: JsValue): BSONArray = BSONArray(json.convertTo[Seq[JsValue]].map(bvalJS.read))
    override def write(obj: BSONArray): JsValue = JsArray(obj.stream.toVector.flatMap(_.map(bvalJS.write).toOption))
  }

  implicit val bdocJS: RootJsonFormat[BSONDocument] = new RootJsonFormat[BSONDocument] {
    override def read(json: JsValue): BSONDocument = BSONDocument(json.convertTo[Map[String, JsValue]].mapValues(bvalJS.read))
    override def write(obj: BSONDocument): JsValue = JsObject(obj.toMap.mapValues(bvalJS.write))
  }
}

object BsonJsonProtocol extends BsonJsonProtocol
