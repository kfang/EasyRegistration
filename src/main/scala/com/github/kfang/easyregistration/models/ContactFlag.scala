package com.github.kfang.easyregistration.models

import com.github.kfang.easyregistration.utils.BsonJsonEnum
import enumeratum.EnumEntry.Hyphencase
import enumeratum._

import scala.collection.immutable

sealed trait ContactFlag extends EnumEntry with Hyphencase

object ContactFlag extends Enum[ContactFlag] with BsonJsonEnum[ContactFlag] {

  val values: immutable.IndexedSeq[ContactFlag] = findValues

  case object Guardian extends ContactFlag
  case object Emergency extends ContactFlag
}
