package de.htwg.se.soccercardclash.model.cardComponent.types

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value.*
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}

class BoostedCardSpec extends AnyWordSpec with Matchers {

  "A BoostedCard" should {

    "correctly apply an additional boost value" in {
      val baseCard = new RegularCard(Five, Hearts)
      val boostedCard = new BoostedCard(baseCard, 3)

      boostedCard.value shouldBe Eight
    }

    "return the original card when reverted" in {
      val baseCard = new RegularCard(Jack, Clubs)
      val boostedCard = new BoostedCard(baseCard, 4)

      val reverted = boostedCard.revertBoost()
      reverted shouldBe a[RegularCard]
      reverted.value shouldBe Jack
      reverted.suit shouldBe Clubs
    }

    "maintain suit after boosting" in {
      val baseCard = new RegularCard(Nine, Spades)
      val boostedCard = new BoostedCard(baseCard, 2)

      boostedCard.suit shouldBe Spades
    }

    "return itself when boost() is called again" in {
      val baseCard = new RegularCard(Ten, Diamonds)
      val boostedCard = new BoostedCard(baseCard, 5)

      boostedCard.boost() shouldBe boostedCard
    }

    "create an independent copy" in {
      val baseCard = new RegularCard(Three, Hearts)
      val boostedCard = new BoostedCard(baseCard, 2)

      val copy = boostedCard.copy()

      copy shouldBe a[BoostedCard]
      copy should not be theSameInstanceAs(boostedCard)
      copy.value shouldBe boostedCard.value
      copy.suit shouldBe boostedCard.suit
    }

    "not change value if boost exceeds max card value" in {
      val baseCard = new RegularCard(King, Diamonds)
      val boostedCard = new BoostedCard(baseCard, 3) // King + 3 (shouldn't exist)

      boostedCard.value shouldBe King // Should stay at King
    }
  }
}