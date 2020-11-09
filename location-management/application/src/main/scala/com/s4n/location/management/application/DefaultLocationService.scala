package com.s4n.location.management.application

import cats.effect.Async
import com.s4n.core.Dsl
import com.s4n.location.management.domain.LocationRepository
import fs2.Stream

final class DefaultLocationService[F[_]: Async] private (
  val locationRepository: LocationRepository[F]
) extends LocationService[F] {

  override def save(drone: Dsl.Drone): Stream[F, Unit] =
    locationRepository.save(drone)
}

object DefaultLocationService {

  def make[F[_]: Async](
    repository: LocationRepository[F]
  ): F[LocationService[F]] =
    Async[F].delay(new DefaultLocationService(repository))
}
