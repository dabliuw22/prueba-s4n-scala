package com.s4n.location.management.adapter

import cats.effect.Async
import com.s4n.core.Dsl._
import com.s4n.infrastructure.file.{File, FileName, Files, Line}
import com.s4n.location.management.adapter.config.LocationRepositoryConfig
import com.s4n.location.management.domain.LocationRepository
import fs2.Stream

final class DefaultLocationRepository[F[_]: Async: Files] private (
  val config: LocationRepositoryConfig.Config
) extends LocationRepository[F] {

  override def save(drone: Drone): Stream[F, Unit] =
    Files[F].write(config.directory, file(drone))

  private def file(drone: Drone): File =
    File(
      FileName("out" + drone.name),
      drone.cmds
        .map(eval)
        .map(view)
        .map(_ + "\n")
        .map(Line(_))
    )

  private def view(position: Position): String =
    s"(${position.coordinates.x.value}, ${position.coordinates.y.value}) direction ${position.direction}"
}

object DefaultLocationRepository {

  def make[F[_]: Async: Files](
    config: LocationRepositoryConfig.Config
  ): F[LocationRepository[F]] =
    Async[F].delay(new DefaultLocationRepository(config))
}
