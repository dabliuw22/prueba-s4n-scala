package com.s4n.location.management.adapter.config

import cats.implicits._
import ciris.ConfigValue
import ciris._
import ciris.refined._
import com.s4n.infrastructure.file.Directory
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString

object LocationRepositoryConfig {

  type OutPath = NonEmptyString

  final case class Config(directory: Directory)

  val config: ConfigValue[Config] =
    env("OUT_PATH")
      .as[OutPath]
      .default("/Users/will/Desktop/out")
      .map(_.value)
      .map(Directory(_))
      .map(Config)
}
