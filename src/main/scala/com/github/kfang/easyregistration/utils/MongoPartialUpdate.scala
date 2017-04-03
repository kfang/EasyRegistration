package com.github.kfang.easyregistration.utils

import reactivemongo.bson.{BSONDocument, BSONValue, BSONWriter}


sealed trait MongoPartialUpdate
case class Set(field: String, value: BSONValue) extends MongoPartialUpdate
case class Unset(field: String) extends MongoPartialUpdate

object MongoPartialUpdate {

  //TODO handle generic BSONDocument
  def apply[B <: BSONValue, T](field: String, value: Option[T], unsetOnEmpty: Boolean = true, defaultValue: Option[T] = None)
                              (implicit writer: BSONWriter[T, B]): Option[MongoPartialUpdate] = {
    value.map({
      case "" if unsetOnEmpty                 => Unset(field)
      case Nil if unsetOnEmpty                => Unset(field)
      case BSONDocument.empty if unsetOnEmpty => Unset(field)
      case v                                  => Set(field, writer.write(v))
    })
  }

}
