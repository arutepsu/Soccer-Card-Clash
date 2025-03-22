package de.htwg.se.soccercardclash.model.cardComponent.base.components

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit

class SuitSpec extends AnyWordSpec with Matchers {

  "Suit" should {

    "contain all four suits" in {
      Suit.allSuits should contain theSameElementsAs List(Suit.Hearts, Suit.Diamonds, Suit.Clubs, Suit.Spades)
    }

    "convert suit to string correctly" in {
      Suit.suitToString(Suit.Hearts) shouldBe "Hearts"
      Suit.suitToString(Suit.Diamonds) shouldBe "Diamonds"
      Suit.suitToString(Suit.Clubs) shouldBe "Clubs"
      Suit.suitToString(Suit.Spades) shouldBe "Spades"
    }

    "convert valid string to suit correctly" in {
      Suit.fromString("Hearts") shouldBe Some(Suit.Hearts)
      Suit.fromString("Diamonds") shouldBe Some(Suit.Diamonds)
      Suit.fromString("Clubs") shouldBe Some(Suit.Clubs)
      Suit.fromString("Spades") shouldBe Some(Suit.Spades)
    }

    "return None for invalid suit string" in {
      Suit.fromString("Invalid") shouldBe None
      Suit.fromString("") shouldBe None
      Suit.fromString("hearts") shouldBe None // Case-sensitive check
    }
  }
}
