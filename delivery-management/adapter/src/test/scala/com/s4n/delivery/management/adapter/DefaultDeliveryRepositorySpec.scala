package com.s4n.delivery.management.adapter

import java.nio.file.{Path, Paths}

import cats.effect.IO
import com.s4n.core.Dsl._
import com.s4n.delivery.management.adapter.config.DeliveryRepositoryConfig
import com.s4n.infrastructure.file._
import com.s4n.test.EffectSpec
import fs2.Stream

final class DefaultDeliveryRepositorySpec extends EffectSpec {

  "DeliveryRepository.findAll()" should {
    "Saved One Position Successfully" in {
      implicit val files = success
      val effect = for {
        location <- DefaultDeliveryRepository.make[IO](config)
        result <- location.findAll
          .compile
          .toList
        routes = List(Init(A(End())))
      } yield assertResult(Drone("test.txt", routes))(result.head)
      effect.unsafeToFuture()
    }
  }

  private def config: DeliveryRepositoryConfig.Config =
    DeliveryRepositoryConfig.Config(Directory("test/"), 1)

  private def success: Files[IO] = new Files[IO] {
    override def create(directory: Directory): IO[Path] =
      IO { Paths.get("test/") }

    override def write(directory: Directory, file: File): Stream[IO, Unit] = ???

    override def read(maxConcurrent: Int)(directory: Directory): Stream[IO, File] =
      Stream.emit(File(FileName("test.txt"), List(Line("A"))))
  }
}
