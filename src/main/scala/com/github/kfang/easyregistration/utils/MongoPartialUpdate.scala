package com.github.kfang.easyregistration.utils

import reactivemongo.bson.{BSONDocument, BSONInteger, BSONValue, BSONWriter}

import scala.collection.mutable.ListBuffer


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

  implicit class UpdateSeqUtil(updates: Seq[MongoPartialUpdate]){
    def toUpdate: Option[BSONDocument] = {
      val sets = BSONDocument(updates.flatMap({
        case pu: Set => Some(pu.field -> pu.value)
        case _ => None
      }))

      val unsets = BSONDocument(updates.flatMap({
        case pu: Unset => Some(pu.field -> BSONInteger(1))
        case _ => None
      }))

      val update = BSONDocument(Seq(
        if(sets.isEmpty) None else Some("$set" -> sets),
        if(unsets.isEmpty) None else Some("$unset" -> unsets)
      ).flatten)

      if(update.isEmpty) None else Some(update)
    }
  }
}

