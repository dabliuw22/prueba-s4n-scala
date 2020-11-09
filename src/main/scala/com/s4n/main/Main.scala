package com.s4n.main

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.s4n.delivery.management.adapter.config.DeliveryRepositoryConfig
import com.s4n.delivery.management.adapter.DefaultDeliveryRepository
import com.s4n.delivery.management.application.DefaultDeliveryService
import com.s4n.delivery.management.application.config.DeliveryConfig
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import com.s4n.infrastructure.file.DefaultFiles
import com.s4n.location.management.adapter.config.LocationRepositoryConfig
import com.s4n.location.management.adapter.DefaultLocationRepository
import com.s4n.location.management.application.DefaultLocationService

object Main extends IOApp {

  private val logger =
    Slf4jLogger.getLoggerFromClass[IO](this.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      DefaultFiles.makeResource[IO](blocker).use { implicit files =>
        for {
          _ <- logger.info("Init..")
          in <- DeliveryRepositoryConfig.config.load[IO]
          out <- LocationRepositoryConfig.config.load[IO]
          _ <- files.create(out.directory)
          locationRepo <- DefaultLocationRepository
                            .make[IO](out)
          location <- DefaultLocationService.make[IO](locationRepo)
          deliveryRepo <- DefaultDeliveryRepository
                            .make[IO](in)
          config <- DeliveryConfig.config.load[IO]
          delivery <-
            DefaultDeliveryService.make[IO](config, deliveryRepo, location)
          _ <- delivery.run.compile.drain
          _ <- logger.info("End..")
        } yield ExitCode.Success
      }
    }
}
