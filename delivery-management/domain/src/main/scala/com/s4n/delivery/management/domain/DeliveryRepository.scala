package com.s4n.delivery.management.domain

import com.s4n.core.Dsl.Drone
import fs2.Stream

trait DeliveryRepository[F[_]] {
  def findAll: Stream[F, Drone]
}
