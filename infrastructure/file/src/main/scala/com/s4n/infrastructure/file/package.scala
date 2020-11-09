package com.s4n.infrastructure

import java.nio.file.{Path, Paths}

import cats.Applicative
import cats.data.Kleisli
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.effect.{Blocker, Concurrent, ContextShift, Resource, Sync}
import simulacrum.typeclass
import fs2.io.file._
import fs2.text._
import fs2.Stream

package object file {

  case class Directory(path: String)

  case class Line(value: String)

  case class File(name: FileName, lines: List[Line])

  case class FileName(value: String)

  @typeclass
  trait Files[F[_]] {
    def create(directory: Directory): F[Path]
    def write(directory: Directory, file: File): Stream[F, Unit]
    def read(maxConcurrent: Int = 1)(directory: Directory): Stream[F, File]
  }

  final class DefaultFiles[F[_]: Sync: ContextShift: Concurrent] private (
    val blocker: Blocker
  ) extends Files[F] {

    override def create(directory: Directory): F[Path] =
      Kleisli[F, Path, Path] { path =>
        exists(blocker, path)
          .flatMap {
            case true =>
              deleteDirectoryRecursively(blocker, makePath(directory.path))
            case _ => Sync[F].unit
          }
          .flatMap(_ => createDirectory(blocker, path))
      }.run(makePath(directory.path))

    override def write(
      directory: Directory,
      file: File
    ): Stream[F, Unit] = {
      Stream
        .emits(file.lines)
        .map(_.value)
        .through(utf8Encode)
        .through(writeAll(makeFilePath(directory, file.name), blocker))
    }

    override def read(
      maxConcurrent: Int = 1
    )(directory: Directory): Stream[F, File] =
      directoryStream(blocker, makePath(directory.path))
        .parEvalMap(maxConcurrent)(readLines)
        .map(tuple => File(tuple._1, tuple._2))

    private def readLines(path: Path): F[(FileName, List[Line])] =
      readAll(path, blocker, 56)
        .through(utf8Decode)
        .through(lines)
        .filter(_.nonEmpty)
        .map(Line)
        .compile
        .toList
        .map(lines => (getName(path), lines))

    private def makeFilePath(
      directory: Directory,
      name: FileName
    ): Path =
      makePath(directory.path + "/" + name.value + ".txt")

    private def makePath(path: String): Path =
      Paths.get(path)

    private def getName(path: Path): FileName =
      FileName(
        path.getFileName.toString
          .replace("in", "")
          .replace(".txt", "")
      )
  }

  object DefaultFiles {

    private def make[F[_]: Sync: ContextShift: Concurrent](
      blocker: Blocker
    ): F[Files[F]] =
      Sync[F].delay(new DefaultFiles[F](blocker))

    def makeResource[F[_]: Sync: ContextShift: Concurrent: Applicative](
      blocker: Blocker
    ): Resource[F, Files[F]] =
      Resource.liftF(make[F](blocker))
  }
}
