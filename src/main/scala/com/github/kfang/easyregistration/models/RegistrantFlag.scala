package com.github.kfang.easyregistration.models

import com.github.kfang.easyregistration.utils.BsonJsonEnum
import enumeratum.EnumEntry.Hyphencase
import enumeratum._

import scala.collection.immutable

sealed trait RegistrantFlag extends EnumEntry with Hyphencase

object RegistrantFlag extends Enum[RegistrantFlag] with BsonJsonEnum[RegistrantFlag] {
  val values: immutable.IndexedSeq[RegistrantFlag] = findValues

  case object Paid extends RegistrantFlag       //payment received
  case object Unpaid extends RegistrantFlag     //payment not received
  case object Refunded extends RegistrantFlag   //payment refunded

  case object Cancelled extends RegistrantFlag  //registrant cancelled
}
