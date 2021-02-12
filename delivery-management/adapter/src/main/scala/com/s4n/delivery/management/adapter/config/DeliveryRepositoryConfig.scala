package com.s4n.delivery.management.adapter.config

import cats.implicits._
import ciris.ConfigValue
import ciris._
import ciris.refined._
import com.s4n.infrastructure.file.Directory
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

object DeliveryRepositoryConfig {

  type InPath = NonEmptyString

  type DeliveryMaxConcurrent = PosInt

  final case class Config(
    directory: Directory,
    maxConcurrent: Int
  )

  val config: ConfigValue[Config] =
    (
      env("IN_PATH")
        .as[InPath]
        .default("/Users/will/Desktop/in")
        .map(_.value)
        .map(Directory(_)),
      env("DELIVERY_MAX_CONCURRENT")
        .as[DeliveryMaxConcurrent]
        .default(5)
        .map(_.value)
    ).parMapN((in, max) => Config(in, max))
}
