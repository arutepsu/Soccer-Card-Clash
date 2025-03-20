package model.cardComponent.base.types

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.base.Card
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.base.components.Suit._
import model.cardComponent.base.components.Value._
import model.cardComponent.base.types.BoostedCard
import model.cardComponent.base.types.RegularCard
import model.cardComponent.boosting.BoostingPolicies

class RegularCardSpec extends AnyWordSpec with Matchers {

  "A RegularCard" should {

    "have the correct initial value and suit" in {
      val card = new RegularCard(Four, Hearts)
      card.value shouldBe Four
      card.suit shouldBe Hearts
    }

    "allow setting a new value" in {
      val card = new RegularCard(Seven, Diamonds)
      card.setValue(Ten)

      card.value shouldBe Ten
    }

    "boost correctly by the defined policy amount" in {
      val card = new RegularCard(Five, Clubs)
      val boosted = card.boost()

      boosted shouldBe a[BoostedCard]
      val boostAmount = BoostingPolicies.getBoostAmount(Five)
      boosted.value shouldBe Value.fromInt(Value.valueToInt(Five) + boostAmount).get
      boosted.suit shouldBe card.suit
    }

    "revertBoost() should return the same card" in {
      val card = new RegularCard(Two, Spades)
      card.revertBoost() shouldBe card
    }

    "create a copy with the same value and suit" in {
      val card = new RegularCard(Queen, Hearts)
      val copy = card.copy()

      copy shouldBe a[RegularCard]
      copy should not be theSameInstanceAs(card)
      copy.value shouldBe card.value
      copy.suit shouldBe card.suit
    }
  }
}
