package com.s4n.core

import monocle.macros.syntax.lens._

import scala.annotation.tailrec

object Dsl {
  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object East extends Direction
  case object West extends Direction

  case class X(value: Int) extends AnyVal
  case class Y(value: Int) extends AnyVal

  case class Coordinates(x: X = X(0), y: Y = Y(0))

  case class Position(
    coordinates: Coordinates = Coordinates(),
    direction: Direction = North
  )

  sealed trait Cmd
  case class Init(next: Cmd) extends Cmd
  case class End(
    previous: Option[Position] = None
  ) extends Cmd
  case class A(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd
  case class I(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd
  case class D(
    next: Cmd,
    previous: Option[Position] = None
  ) extends Cmd

  case class Drone(name: String, cmds: List[Cmd])

  def makeRoute(route: String): Cmd =
    Init(makeRouteR(route.toList.reverse)())

  @tailrec
  private def makeRouteR(chars: List[Char])(route: Cmd = End()): Cmd =
    chars match {
      case h :: t =>
        h match {
          case 'A' => makeRouteR(t)(A(route))
          case 'I' => makeRouteR(t)(I(route))
          case 'D' => makeRouteR(t)(D(route))
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
    action: Cmd,
    previous: Position
  ): Cmd =
    action match {
      case A(next, _) => A(next, Some(previous))
      case I(next, _) => I(next, Some(previous))
      case D(next, _) => D(next, Some(previous))
      case End(_)     => End(Some(previous))
      case _          => action
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
        Position(previous.lens(_.y.value).modify(_ + 1), North)
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
        Position(previous.lens(_.y.value).modify(_ + (-1)), South)
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
        Position(previous.lens(_.x.value).modify(_ + 1), West)
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
        Position(previous.lens(_.x.value).modify(_ + (-1)), East)
      case I(_, _) => Position(previous, South)
      case D(_, _) => Position(previous, North)
      case _       => Position(previous, East)
    }
}
