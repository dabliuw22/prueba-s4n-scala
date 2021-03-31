package com.s4n.location.management.adapter

import java.nio.file.Path

import cats.effect.IO
import com.s4n.core.Dsl._
import com.s4n.infrastructure.file._
import com.s4n.location.management.adapter.config.LocationRepositoryConfig
import com.s4n.test.EffectSpec
import fs2.Stream

final class DefaultLocationRepositorySpec extends EffectSpec {

  "LocationRepository.save()" should {
    "Saved One Position Successfully" in {
      implicit val files = success
      val effect = for {
        location <- DefaultLocationRepository.make[IO](config)
        result <-
          location
            .save(
              Drone("test-drone", List(Init.make(A.make(A.make(End.make())))))
            )
            .compile
            .toList
        status = result.size == 1
      } yield assert(status)
      effect.unsafeToFuture()
    }
  }

  "LocationRepository.save()" should {
    "Saved Two Position Successfully" in {
      implicit val files = success
      val effect = for {
        location <- DefaultLocationRepository.make[IO](config)
        result <- location
                    .save(
                      Drone(
                        "test-drone",
                        List(
                          Init.make(A.make(A.make(End.make()))),
                          Init.make(A.make(End.make()))
                        )
                      )
                    )
                    .compile
                    .toList
        status = result.size == 2
      } yield assert(status == true)
      effect.unsafeToFuture()
    }
  }

  "LocationRepository.save()" should {
    "Saved Failure" in {
      implicit val files = failure
      val effect = for {
        location <- DefaultLocationRepository.make[IO](config)
        status <-
          location
            .save(
              Drone("test-drone", List(Init.make(A.make(A.make(End.make())))))
            )
            .map(_ => false)
            .compile
            .toList
            .handleErrorWith(_ => IO(true))
      } yield assert(status == true)
      effect.unsafeToFuture()
    }
  }

  private def config: LocationRepositoryConfig.Config =
    LocationRepositoryConfig.Config(Directory("/test"))

  private def success: Files[IO] =
    new Files[IO] {
      override def create(directory: Directory): IO[Path] = ???

      override def write(directory: Directory, file: File): Stream[IO, Unit] =
        Stream
          .emits(file.lines)
          .map(_ => ())
          .covary[IO]

      override def read(maxConcurrent: Int)(
        directory: Directory
      ): Stream[IO, File] = ???
    }

  private def failure: Files[IO] =
    new Files[IO] {
      override def create(directory: Directory): IO[Path] = ???

      override def write(directory: Directory, file: File): Stream[IO, Unit] =
        Stream.raiseError[IO](new RuntimeException)

      override def read(maxConcurrent: Int)(
        directory: Directory
      ): Stream[IO, File] = ???
    }
}
