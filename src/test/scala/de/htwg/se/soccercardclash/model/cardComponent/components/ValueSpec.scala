package de.htwg.se.soccercardclash.model.cardComponent.components

import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ValueSpec extends AnyWordSpec with Matchers {

  "Value" should {

    "contain all 13 card values" in {
      Value.allValues should contain theSameElementsAs List(
        Value.Ace, Value.Two, Value.Three, Value.Four, Value.Five, Value.Six, Value.Seven,
        Value.Eight, Value.Nine, Value.Ten, Value.Jack, Value.Queen, Value.King
      )
    }

    "convert value to integer correctly" in {
      Value.valueToInt(Value.Ace) shouldBe 14
      Value.valueToInt(Value.Two) shouldBe 2
      Value.valueToInt(Value.Three) shouldBe 3
      Value.valueToInt(Value.Four) shouldBe 4
      Value.valueToInt(Value.Five) shouldBe 5
      Value.valueToInt(Value.Six) shouldBe 6
      Value.valueToInt(Value.Seven) shouldBe 7
      Value.valueToInt(Value.Eight) shouldBe 8
      Value.valueToInt(Value.Nine) shouldBe 9
      Value.valueToInt(Value.Ten) shouldBe 10
      Value.valueToInt(Value.Jack) shouldBe 11
      Value.valueToInt(Value.Queen) shouldBe 12
      Value.valueToInt(Value.King) shouldBe 13
    }

    "convert integer to Value correctly" in {
      Value.fromInt(14) shouldBe Some(Value.Ace)
      Value.fromInt(2) shouldBe Some(Value.Two)
      Value.fromInt(3) shouldBe Some(Value.Three)
      Value.fromInt(10) shouldBe Some(Value.Ten)
      Value.fromInt(13) shouldBe Some(Value.King)
    }

    "return None for invalid integer values" in {
      Value.fromInt(1) shouldBe None
      Value.fromInt(15) shouldBe None
      Value.fromInt(0) shouldBe None
    }

    "convert string to Value correctly" in {
      Value.fromString("Ace") shouldBe Some(Value.Ace)
      Value.fromString("2") shouldBe Some(Value.Two)
      Value.fromString("Jack") shouldBe Some(Value.Jack)
      Value.fromString("Queen") shouldBe Some(Value.Queen)
      Value.fromString("King") shouldBe Some(Value.King)
    }

    "return None for invalid string values" in {
      Value.fromString("Joker") shouldBe None
      Value.fromString("1") shouldBe None
      Value.fromString("") shouldBe None
      Value.fromString("Acee") shouldBe None
    }

    "compare values correctly" in {
      (Value.Ace > Value.King) shouldBe true
      (Value.Two < Value.Three) shouldBe true
      (Value.Ten > Value.Six) shouldBe true
      (Value.Jack < Value.Queen) shouldBe true
      (Value.King > Value.Queen) shouldBe true
      (Value.Four == Value.Four) shouldBe true
    }
  }
}