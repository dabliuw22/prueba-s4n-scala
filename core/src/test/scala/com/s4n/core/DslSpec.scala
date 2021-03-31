package com.s4n.core

import com.s4n.core.Dsl._
import com.s4n.test.Spec

final class DslSpec extends Spec {

  "makeCmd(AAAAIAA)" should {
    "Return Init(A(A(A(A(I(A(A(End()))))))))" in {
      val expected =
        Init.make(
          A.make(A.make(A.make(A.make(I.make(A.make(A.make(End.make())))))))
        )
      makeCmd("AAAAIAA") shouldBe expected
    }
  }

  "eval(AADADA)" should {
    "Return (1, 1) South" in {
      val route =
        Init.make(A.make(A.make(D.make(A.make(D.make(A.make(End.make())))))))
      val expected =
        Position.make(Coordinates.make(X.make(1), Y.make(1)), South)
      eval(route) shouldBe expected
    }
  }

  "makeCmd(AADADA)" should {
    "Return Init(A(A(D(A(D(A(End())))))))" in {
      val expected =
        Init.make(A.make(A.make(D.make(A.make(D.make(A.make(End.make())))))))
      makeCmd("AADADA") shouldBe expected
    }
  }

  "eval(AAAAIAA)" should {
    "Return (-2, 4) East" in {
      val route =
        Init.make(
          A.make(A.make(A.make(A.make(I.make(A.make(A.make(End.make())))))))
        )
      val expected =
        Position.make(Coordinates.make(X.make(-2), Y.make(4)), East)
      eval(route) shouldBe expected
    }
  }

  "makeCmd(DDDAIAD)" should {
    "Return Init(D(D(D(A(I(A(D(End()))))))))" in {
      val expected =
        Init.make(
          D.make(D.make(D.make(A.make(I.make(A.make(D.make(End.make())))))))
        )
      makeCmd("DDDAIAD") shouldBe expected
    }
  }

  "eval(DDDAIAD)" should {
    "Return (-1, -1) East" in {
      val route =
        Init.make(
          D.make(D.make(D.make(A.make(I.make(A.make(D.make(End.make())))))))
        )
      val expected =
        Position.make(Coordinates.make(X.make(-1), Y.make(-1)), East)
      eval(route) shouldBe expected
    }
  }

  "makeCmd(AAIADAD)" should {
    "Return Init(A(A(I(A(D(A(D(End()))))))))" in {
      val expected =
        Init.make(
          A.make(A.make(I.make(A.make(D.make(A.make(D.make(End.make())))))))
        )
      makeCmd("AAIADAD") shouldBe expected
    }
  }

  "eval(AAIADAD)" should {
    "Return (-1, 3) West" in {
      val route =
        Init.make(
          A.make(A.make(I.make(A.make(D.make(A.make(D.make(End.make())))))))
        )
      val expected =
        Position.make(Coordinates.make(X.make(-1), Y.make(3)), West)
      eval(route) shouldBe expected
    }
  }

  "makeCmd(A)" should {
    "Return Init(A(End()))" in {
      val expected = Init.make(A.make(End.make()))
      makeCmd("A") shouldBe expected
    }
  }

  "eval(A)" should {
    "Return (0, 1) East" in {
      val route = Init.make(A.make(End.make()))
      val expected = Position(Coordinates.make(X.make(0), Y.make(1)), North)
      eval(route) shouldBe expected
    }
  }
}
