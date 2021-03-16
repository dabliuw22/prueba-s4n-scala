package com.s4n.core

import cats.syntax.option._
import monocle.macros.GenIso
import monocle.{Lens, Optional, Prism}
import monocle.macros.{GenLens, GenPrism}

import scala.annotation.tailrec

object Dsl {
  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object East extends Direction
  case object West extends Direction

  final case class X(value: Int = 0) extends AnyVal
  object X {
    val value: Lens[X, Int] = GenLens[X](_.value)
  }
  final case class Y(value: Int = 0) extends AnyVal
  object Y {
    val value: Lens[Y, Int] = GenLens[Y](_.value)
  }

  final case class Coordinates(x: X = X(), y: Y = Y())
  object Coordinates {
    val x: Lens[Coordinates, Int] =
      GenLens[Coordinates](_.x) composeLens X.value
    val y: Lens[Coordinates, Int] =
      GenLens[Coordinates](_.y) composeLens Y.value
  }

  final case class Position(
    coordinates: Coordinates = Coordinates(),
    direction: Direction = North
  )
  object Position {
    val coordinates: Lens[Position, Coordinates] =
      GenLens[Position](_.coordinates)
    val direction: Lens[Position, Direction] =
      GenLens[Position](_.direction)
  }

  sealed trait Cmd
  final case class Init(next: Cmd) extends Cmd
  object Init {
    val next: Prism[Cmd, Cmd] =
      GenPrism[Cmd, Init] composeIso GenIso[Init, Cmd]
  }
  final case class End(
    previous: Option[Position] = None
  ) extends Cmd
  object End {
    val previous: Prism[Cmd, Option[Position]] =
      GenPrism[Cmd, End] composeIso GenIso[End, Option[Position]]
  }
  final case class A(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd
  object A {
    private val a: Prism[Cmd, A] = GenPrism[Cmd, A]
    val next: Optional[Cmd, Cmd] =
      a composeLens GenLens[A](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      a composeLens GenLens[A](_.previous)
  }
  final case class I(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd
  object I {
    private val i: Prism[Cmd, I] = GenPrism[Cmd, I]
    val next: Optional[Cmd, Cmd] =
      i composeLens GenLens[I](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      i composeLens GenLens[I](_.previous)
  }
  final case class D(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd
  object D {
    private val d: Prism[Cmd, D] = GenPrism[Cmd, D]
    val next: Optional[Cmd, Cmd] =
      d composeLens GenLens[D](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      d composeLens GenLens[D](_.previous)
  }

  final case class Drone(name: String, cmds: List[Cmd])

  def makeCmd(route: String): Cmd =
    Init(makeCmdR(route.toList.reverse)())

  @tailrec
  private def makeCmdR(chars: List[Char])(route: Cmd = End()): Cmd =
    chars match {
      case h :: t =>
        h match {
          case 'A' => makeCmdR(t)(A(route))
          case 'I' => makeCmdR(t)(I(route))
          case 'D' => makeCmdR(t)(D(route))
          case _   => throw new RuntimeException("Invalid Char Input")
        }
      case Nil => route
    }

  @tailrec
  def eval(action: Cmd): Position =
    action match {
      case Init(n)     => eval(update(n, Position()))
      case a @ A(n, p) => eval(update(n, calculate(a, p.get)))
      case i @ I(n, p) => eval(update(n, calculate(i, p.get)))
      case d @ D(n, p) => eval(update(n, calculate(d, p.get)))
      case End(p)      => p.get
    }

  private def update(
    cmd: Cmd,
    previous: Position
  ): Cmd =
    cmd match {
      case a: A     => A.previous.set(previous.some)(a)
      case i: I     => I.previous.set(previous.some)(i)
      case d: D     => D.previous.set(previous.some)(d)
      case end: End => End.previous.set(previous.some)(end)
      case _        => cmd
    }

  private def calculate(
    action: Cmd,
    previous: Position
  ): Position =
    previous match {
      case Position(coordinates, direction) =>
        direction match {
          case North => fromNorth(action, coordinates)
          case East  => fromEast(action, coordinates)
          case West  => fromWest(action, coordinates)
          case _     => fromSouth(action, coordinates)
        }
    }

  private def fromNorth(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates.y.modify(_ + 1)(previous), North)
      case I(_, _) => Position(previous, East)
      case D(_, _) => Position(previous, West)
      case _       => Position(previous, North)
    }

  private def fromSouth(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates.y.modify(_ - 1)(previous), South)
      case I(_, _) => Position(previous, West)
      case D(_, _) => Position(previous, East)
      case _       => Position(previous, South)
    }

  private def fromWest(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates.x.modify(_ + 1)(previous), West)
      case I(_, _) => Position(previous, North)
      case D(_, _) => Position(previous, South)
      case _       => Position(previous, West)
    }

  private def fromEast(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates.x.modify(_ - 1)(previous), East)
      case I(_, _) => Position(previous, South)
      case D(_, _) => Position(previous, North)
      case _       => Position(previous, East)
    }
}
