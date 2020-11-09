package com.s4n.location.management.application

import com.s4n.core.Dsl.Drone
import fs2.Stream

trait LocationService[F[_]] {
  def save(drone: Drone): Stream[F, Unit]
}
