package com.s4n.delivery.management.application

import com.s4n.core.Dsl.Drone
import fs2.Stream

trait DeliveryService[F[_]] {
  def run: Stream[F, Unit]
}
