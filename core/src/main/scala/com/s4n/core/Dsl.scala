package com.s4n.core

import cats.syntax.option._
import monocle.{Iso, Lens, Optional, Prism}
import monocle.macros.{GenIso, GenLens, GenPrism}

import scala.annotation.tailrec

object Dsl {
  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object East extends Direction
  case object West extends Direction

  final case class X private (value: Int) extends AnyVal
  object X {
    val value: Iso[X, Int] = GenIso[X, Int]
    def make(value: Int = 0): X = X(value)
  }
  final case class Y private (value: Int) extends AnyVal
  object Y {
    val value: Iso[Y, Int] = GenIso[Y, Int]
    def make(value: Int = 0): Y = Y(value)
  }

  final case class Coordinates private (x: X, y: Y)
  object Coordinates {
    val x: Lens[Coordinates, Int] =
      GenLens[Coordinates](_.x) composeIso X.value
    val y: Lens[Coordinates, Int] =
      GenLens[Coordinates](_.y) composeIso Y.value
    def make(x: X = X.make(), y: Y = Y.make()): Coordinates =
      Coordinates(x, y)
  }

  final case class Position private (
    coordinates: Coordinates,
    direction: Direction
  )
  object Position {
    val coordinates: Lens[Position, Coordinates] =
      GenLens[Position](_.coordinates)
    val direction: Lens[Position, Direction] =
      GenLens[Position](_.direction)
    def make(
      coordinates: Coordinates = Coordinates.make(),
      direction: Direction = North
    ): Position =
      Position(coordinates, direction)
  }

  sealed trait Cmd
  final case class Init private (next: Cmd) extends Cmd
  object Init {
    val next: Prism[Cmd, Cmd] =
      GenPrism[Cmd, Init] composeIso GenIso[Init, Cmd]
    val make: Cmd => Cmd = next => Init(next)
  }
  final case class End private (
    previous: Option[Position]
  ) extends Cmd
  object End {
    val previous: Prism[Cmd, Option[Position]] =
      GenPrism[Cmd, End] composeIso GenIso[End, Option[Position]]
    def make(previous: Option[Position] = None): Cmd =
      End(previous)
  }
  final case class A private (
    next: Cmd,
    previous: Option[Position]
  ) extends Cmd
  object A {
    private val a: Prism[Cmd, A] = GenPrism[Cmd, A]
    val next: Optional[Cmd, Cmd] =
      a composeLens GenLens[A](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      a composeLens GenLens[A](_.previous)
    def make(next: Cmd, previous: Option[Position] = None): Cmd =
      A(next, previous)
  }
  final case class I private (
    next: Cmd,
    previous: Option[Position]
  ) extends Cmd
  object I {
    private val i: Prism[Cmd, I] = GenPrism[Cmd, I]
    val next: Optional[Cmd, Cmd] =
      i composeLens GenLens[I](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      i composeLens GenLens[I](_.previous)
    def make(next: Cmd, previous: Option[Position] = None): Cmd =
      I(next, previous)
  }
  final case class D private (
    next: Cmd,
    previous: Option[Position]
  ) extends Cmd
  object D {
    private val d: Prism[Cmd, D] = GenPrism[Cmd, D]
    val next: Optional[Cmd, Cmd] =
      d composeLens GenLens[D](_.next)
    val previous: Optional[Cmd, Option[Position]] =
      d composeLens GenLens[D](_.previous)
    def make(next: Cmd, previous: Option[Position] = None): Cmd =
      D(next, previous)
  }

  final case class Drone(name: String, cmds: List[Cmd])

  def makeCmd(route: String): Cmd =
    Init(makeCmdR(route.toList.reverse)())

  @tailrec
  private def makeCmdR(chars: List[Char])(route: Cmd = End.make()): Cmd =
    chars match {
      case h :: t =>
        h match {
          case 'A' => makeCmdR(t)(A.make(route))
          case 'I' => makeCmdR(t)(I.make(route))
          case 'D' => makeCmdR(t)(D.make(route))
          case _   => throw new RuntimeException("Invalid Char Input")
        }
      case Nil => route
    }

  @tailrec
  def eval(action: Cmd): Position =
    action match {
      case Init(n)     => eval(update(n, Position.make()))
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
      case _: A =>
        Position(Coordinates.y.modify(_ + 1)(previous), North)
      case _: I => Position(previous, East)
      case _: D => Position(previous, West)
      case _    => Position(previous, North)
    }

  private def fromSouth(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case _: A =>
        Position(Coordinates.y.modify(_ - 1)(previous), South)
      case _: I => Position(previous, West)
      case _: D => Position(previous, East)
      case _    => Position(previous, South)
    }

  private def fromWest(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case _: A =>
        Position(Coordinates.x.modify(_ + 1)(previous), West)
      case _: I => Position(previous, North)
      case _: D => Position(previous, South)
      case _    => Position(previous, West)
    }

  private def fromEast(
    action: Cmd,
    previous: Coordinates
  ): Position =
    action match {
      case _: A =>
        Position(Coordinates.x.modify(_ - 1)(previous), East)
      case _: I => Position(previous, South)
      case _: D => Position(previous, North)
      case _    => Position(previous, East)
    }
}
