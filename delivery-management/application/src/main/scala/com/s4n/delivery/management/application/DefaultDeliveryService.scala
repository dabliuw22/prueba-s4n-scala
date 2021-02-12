package com.s4n.delivery.management.application

import cats.effect.Async
import cats.syntax.functor._
import com.s4n.core.Dsl.{Coordinates, Drone, eval}
import com.s4n.delivery.management.domain.DeliveryRepository
import com.s4n.delivery.management.application.config.DeliveryConfig
import com.s4n.location.management.application.LocationService
import fs2.Stream
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

final class DefaultDeliveryService[F[_]: Async] private (
  val config: DeliveryConfig.Config,
  val repository: DeliveryRepository[F],
  val location: LocationService[F]
) extends DeliveryService[F] {

  private val logger =
    Slf4jLogger.getLoggerFromClass[F](this.getClass)

  override def run: Stream[F, Unit] = {
    repository.findAll
      .evalMap(evalDrone)
      .map(evalRange)
      .handleErrorWith(errorHandler)
      .evalMap(drone => logger.info(s"Drone: $drone").as(drone))
      .flatMap(location.save)
  }

  private def evalDrone(drone: Drone): F[Drone] =
    if (drone.cmds.length > config.limit)
      Async[F].raiseError[Drone](new RuntimeException("Invalid File Length"))
    else Async[F].pure(drone)

  private def evalRange(drone: Drone): Drone = {
    val routes = drone.cmds
      .map(route => (eval(route), route))
      .filter(tuple => evalCoordinates(tuple._1.coordinates))
      .map(_._2)
    drone.copy(cmds = routes)
  }

  private def evalCoordinates(coordinates: Coordinates): Boolean =
    (coordinates.x.value <= config.range && coordinates.x.value >= -config.range
      && coordinates.y.value <= config.range && coordinates.y.value >= -config.range)

  private def errorHandler(error: Throwable): Stream[F, Drone] =
    Stream.eval(logger.error(s"Error: $error")) >> Stream.empty
}

object DefaultDeliveryService {

  def make[F[_]: Async](
    config: DeliveryConfig.Config,
    repository: DeliveryRepository[F],
    location: LocationService[F]
  ): F[DeliveryService[F]] =
    Async[F].delay(new DefaultDeliveryService(config, repository, location))
}
