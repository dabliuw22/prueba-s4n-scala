package com.s4n.core

import io.estatico.newtype.macros.newtype

import scala.annotation.tailrec

object Dsl {
  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object East extends Direction
  case object West extends Direction

  @newtype case class X(value: Int)
  @newtype case class Y(value: Int)

  case class Coordinates(x: X = X(0), y: Y = Y(0))

  case class Position(
    coordinates: Coordinates = Coordinates(),
    direction: Direction = North
  )

  sealed trait Route
  case class Init(next: Route) extends Route
  case class End(
    previous: Option[Position] = None
  ) extends Route
  case class A(
    next: Route,
    previous: Option[Position] = None
  ) extends Route
  case class I(
    next: Route,
    previous: Option[Position] = None
  ) extends Route
  case class D(
    next: Route,
    previous: Option[Position] = None
  ) extends Route

  case class Drone(name: String, routes: List[Route])

  def makeRoute(route: String): Route =
    Init(makeRouteR(route.toCharArray.reverse))

  @tailrec
  private def makeRouteR(
    chars: Array[Char],
    route: Route = End(),
    position: Int = 0
  ): Route =
    if (position < chars.length) chars(position) match {
      case 'A' => makeRouteR(chars, A(route), position + 1)
      case 'I' => makeRouteR(chars, I(route), position + 1)
      case 'D' => makeRouteR(chars, D(route), position + 1)
      case _   => throw new RuntimeException("Invalid Char Input")
    }
    else route

  @tailrec
  def eval(action: Route): Position =
    action match {
      case Init(n)     => eval(update(n, Position()))
      case a @ A(n, p) => eval(update(n, calculate(a, p.get)))
      case i @ I(n, p) => eval(update(n, calculate(i, p.get)))
      case d @ D(n, p) => eval(update(n, calculate(d, p.get)))
      case End(p)      => p.get
    }

  private def update(
    action: Route,
    previous: Position
  ): Route =
    action match {
      case A(next, _) => A(next, Some(previous))
      case I(next, _) => I(next, Some(previous))
      case D(next, _) => D(next, Some(previous))
      case End(_)     => End(Some(previous))
      case _          => action
    }

  private def calculate(
    action: Route,
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
    action: Route,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates(previous.x, Y(previous.y.value + 1)), North)
      case I(_, _) => Position(previous, East)
      case D(_, _) => Position(previous, West)
      case _       => Position(previous, North)
    }

  private def fromSouth(
    action: Route,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates(previous.x, Y(previous.y.value - 1)), South)
      case I(_, _) => Position(previous, West)
      case D(_, _) => Position(previous, East)
      case _       => Position(previous, South)
    }

  private def fromWest(
    action: Route,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates(X(previous.x.value + 1), previous.y), West)
      case I(_, _) => Position(previous, North)
      case D(_, _) => Position(previous, South)
      case _       => Position(previous, West)
    }

  private def fromEast(
    action: Route,
    previous: Coordinates
  ): Position =
    action match {
      case A(_, _) =>
        Position(Coordinates(X(previous.x.value - 1), previous.y), East)
      case I(_, _) => Position(previous, South)
      case D(_, _) => Position(previous, North)
      case _       => Position(previous, East)
    }
}
