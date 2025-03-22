package de.htwg.se.soccercardclash.model.cardComponent.base

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.Card
import de.htwg.se.soccercardclash.model.cardComponent.base.types.RegularCard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit._
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value._

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {

    "be created using the apply method" in {
      val card = Card(Ten, Hearts)
      card shouldBe a[RegularCard]
      card.value shouldBe Ten
      card.suit shouldBe Hearts
    }

    "have a correct string representation" in {
      val card = new RegularCard(Jack, Diamonds)
      card.toString shouldBe "Jack of Diamonds"
    }

    "compare correctly with other cards" in {
      val ace = new RegularCard(Ace, Spades)
      val two = new RegularCard(Two, Spades)
      val king = new RegularCard(King, Clubs)

      ace.compare(two) shouldBe -1
      two.compare(ace) shouldBe 1
      king.compare(ace) shouldBe -1
    }

    "return correct valueToInt conversion" in {
      val queen = new RegularCard(Queen, Hearts)
      queen.valueToInt shouldBe 12
    }

    "generate the correct file name" in {
      val card = new RegularCard(Four, Clubs)
      card.fileName shouldBe "4_of_clubs.png"
    }

    "create a copy with the same value and suit" in {
      val original = new RegularCard(Nine, Spades)
      val copy = original.copy()

      copy shouldBe a[RegularCard]
      copy should not be theSameInstanceAs(original)
      copy.value shouldBe original.value
      copy.suit shouldBe original.suit
    }

    "be equal to another card with the same value and suit" in {
      val card1 = new RegularCard(Seven, Diamonds)
      val card2 = new RegularCard(Seven, Diamonds)
      val card3 = new RegularCard(Eight, Diamonds)

      card1 shouldBe card2
      card1 should not be card3
    }

    "generate consistent hash codes" in {
      val card1 = new RegularCard(Seven, Diamonds)
      val card2 = new RegularCard(Seven, Diamonds)
      val card3 = new RegularCard(King, Hearts)

      card1.hashCode() shouldBe card2.hashCode()
      card1.hashCode() should not be card3.hashCode()
    }
  }
}
