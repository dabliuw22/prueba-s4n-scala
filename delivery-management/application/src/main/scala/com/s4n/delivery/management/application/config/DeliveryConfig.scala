package com.s4n.delivery.management.application.config

import cats.implicits._
import ciris.ConfigValue
import ciris._
import ciris.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt

object DeliveryConfig {

  type DeliveryLimit = PosInt

  type DeliveryRange = PosInt

  final case class Config(
    limit: Int,
    range: Int
  )

  val config: ConfigValue[Config] =
    (
      env("DELIVERY_LIMIT")
        .as[DeliveryLimit]
        .default(3)
        .map(_.value),
      env("DELIVERY_RANGE")
        .as[DeliveryRange]
        .default(10)
        .map(_.value)
    ).parMapN((limit, range) => Config(limit, range))
}
