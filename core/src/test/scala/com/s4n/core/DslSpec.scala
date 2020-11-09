package com.s4n.core

import com.s4n.core.Dsl._
import com.s4n.test.Spec

final class DslSpec extends Spec {

  "makeRoute(AAAAIAA)" should {
    "Return Init(A(A(A(A(I(A(A(End()))))))))" in {
      val expected = Init(A(A(A(A(I(A(A(End()))))))))
      makeRoute("AAAAIAA") shouldBe expected
    }
  }

  "eval(AADADA)" should {
    "Return (1, 1) South" in {
      val route = Init(A(A(D(A(D(A(End())))))))
      val expected = Position(Coordinates(X(1), Y(1)), South)
      eval(route) shouldBe expected
    }
  }

  "makeRoute(AADADA)" should {
    "Return Init(A(A(D(A(D(A(End())))))))" in {
      val expected = Init(A(A(D(A(D(A(End())))))))
      makeRoute("AADADA") shouldBe expected
    }
  }

  "eval(AAAAIAA)" should {
    "Return (-2, 4) East" in {
      val route = Init(A(A(A(A(I(A(A(End()))))))))
      val expected = Position(Coordinates(X(-2), Y(4)), East)
      eval(route) shouldBe expected
    }
  }

  "makeRoute(DDDAIAD)" should {
    "Return Init(D(D(D(A(I(A(D(End()))))))))" in {
      val expected = Init(D(D(D(A(I(A(D(End()))))))))
      makeRoute("DDDAIAD") shouldBe expected
    }
  }

  "eval(DDDAIAD)" should {
    "Return (-1, -1) East" in {
      val route = Init(D(D(D(A(I(A(D(End()))))))))
      val expected = Position(Coordinates(X(-1), Y(-1)), East)
      eval(route) shouldBe expected
    }
  }

  "makeRoute(AAIADAD)" should {
    "Return Init(A(A(I(A(D(A(D(End()))))))))" in {
      val expected = Init(A(A(I(A(D(A(D(End()))))))))
      makeRoute("AAIADAD") shouldBe expected
    }
  }

  "eval(AAIADAD)" should {
    "Return (-1, 3) East" in {
      val route = Init(A(A(I(A(D(A(D(End()))))))))
      val expected = Position(Coordinates(X(-1), Y(3)), West)
      eval(route) shouldBe expected
    }
  }

  "makeRoute(A)" should {
    "Return Init(A(End()))" in {
      val expected = Init(A(End()))
      makeRoute("A") shouldBe expected
    }
  }

  "eval(A)" should {
    "Return (0, 1) East" in {
      val route = Init(A(End()))
      val expected = Position(Coordinates(X(0), Y(1)), North)
      eval(route) shouldBe expected
    }
  }
}
