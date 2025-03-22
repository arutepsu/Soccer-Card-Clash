//package model.cardComponent
//
//import model.cardComponent.base.components.Suit
//import model.cardComponent.base.types.*
//import model.cardComponent.base.impl.*
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.wordspec.AnyWordSpec
//import play.api.libs.json.Json
//import model.cardComponent.base.components.Value
//
//class CardSpec extends AnyWordSpec with Matchers {
//
//  "A RegularCard" should {
//
//    "have correct value and suit" in {
//      val card = new RegularCard(Value.Seven, Suit.Clubs)
//      card.value shouldBe Value.Seven
//      card.suit shouldBe Suit.Clubs
//    }
//
//    "allow boosting into a BoostedCard with increased value" in {
//      val card = new RegularCard(Value.Two, Suit.Hearts)
//      val boosted = card.boost()
//      boosted shouldBe a[BoostedCard]
//      boosted.valueToInt should be > card.valueToInt
//    }
//
//    "revertBoost to itself" in {
//      val card = new RegularCard(Value.Five, Suit.Spades)
//      card.revertBoost() shouldBe card
//    }
//
//    "copy itself with same value and suit" in {
//      val card = new RegularCard(Value.Eight, Suit.Diamonds)
//      val copied = card.copy()
//      copied should not be theSameInstanceAs(card)
//      copied.value shouldBe Value.Eight
//      copied.suit shouldBe Suit.Diamonds
//    }
//  }
//
//  "A BoostedCard" should {
//
//    "correctly calculate boosted value" in {
//      val regular = new RegularCard(Value.Four, Suit.Clubs)
//      val boosted = new BoostedCard(regular, 2)
//      boosted.valueToInt shouldBe Value.valueToInt(Value.Four) + 2
//    }
//
//    "revert back to original RegularCard" in {
//      val regular = new RegularCard(Value.Jack, Suit.Spades)
//      val boosted = new BoostedCard(regular, 1)
//      val reverted = boosted.revertBoost()
//      reverted shouldBe a[RegularCard]
//      reverted.value shouldBe Value.Jack
//      reverted.suit shouldBe Suit.Spades
//    }
//
//    "copy itself with the same base and additionalValue" in {
//      val regular = new RegularCard(Value.Queen, Suit.Hearts)
//      val boosted = new BoostedCard(regular, 3)
//      val copy = boosted.copy()
//      copy shouldBe a[BoostedCard]
//      copy.value shouldBe boosted.value
//      copy.suit shouldBe boosted.suit
//    }
//  }
//}
