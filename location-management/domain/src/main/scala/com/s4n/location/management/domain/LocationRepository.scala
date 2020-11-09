package com.s4n.location.management.domain

import com.s4n.core.Dsl.Drone
import fs2.Stream

trait LocationRepository[F[_]] {
  def save(drone: Drone): Stream[F, Unit]
}
