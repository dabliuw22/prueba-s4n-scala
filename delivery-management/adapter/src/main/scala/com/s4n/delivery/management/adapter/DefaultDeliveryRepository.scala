package com.s4n.delivery.management.adapter

import cats.effect.Async
import com.s4n.core.Dsl._
import com.s4n.delivery.management.adapter.config.DeliveryRepositoryConfig
import com.s4n.delivery.management.domain.DeliveryRepository
import com.s4n.infrastructure.file.Files
import fs2.Stream

final class DefaultDeliveryRepository[F[_]: Async: Files] private (
  val config: DeliveryRepositoryConfig.Config
) extends DeliveryRepository[F] {

  override def findAll: Stream[F, Drone] =
    Files[F]
      .read(config.maxConcurrent)(config.directory)
      .map(file =>
        Drone(
          file.name.value,
          file.lines.map(line => makeRoute(line.value))
        )
      )
}

object DefaultDeliveryRepository {

  def make[F[_]: Async: Files](
    config: DeliveryRepositoryConfig.Config
  ): F[DeliveryRepository[F]] =
    Async[F].delay(new DefaultDeliveryRepository(config))
}
