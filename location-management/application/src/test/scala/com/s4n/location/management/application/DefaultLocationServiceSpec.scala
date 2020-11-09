package com.s4n.location.management.application

import cats.effect.IO
import com.s4n.core.Dsl._
import com.s4n.location.management.domain.LocationRepository
import com.s4n.test.EffectSpec
import fs2.Stream

class DefaultLocationServiceSpec extends EffectSpec {

  "LocationService.save()" should {
    "Saved One Position Successfully" in {
      val effect = for {
        location <- DefaultLocationService.make[IO](success(()))
        result <- location
                    .save(Drone("test-drone", List(Init(A(A(End()))))))
                    .compile
                    .toList
        status = result.size == 1
      } yield assert(status)
      effect.unsafeToFuture()
    }
  }

  "LocationService.save()" should {
    "Saved Two Position Successfully" in {
      val effect = for {
        location <- DefaultLocationService.make[IO](success((), ()))
        result <-
          location
            .save(Drone("test-drone", List(Init(A(A(End()))), Init(A(End())))))
            .compile
            .toList
        status = result.size == 2
      } yield assert(status)
      effect.unsafeToFuture()
    }
  }

  private def success(result: Unit*): LocationRepository[IO] =
    new LocationRepository[IO]() {
      override def save(drone: Drone): Stream[IO, Unit] =
        Stream.emits(result).covary[IO]
    }
}
