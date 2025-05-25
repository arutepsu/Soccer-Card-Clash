package de.htwg.se.soccercardclash.model.cardComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Suit
import de.htwg.se.soccercardclash.model.cardComponent.base.components.Value
import de.htwg.se.soccercardclash.model.cardComponent.base.types.RegularCard
import de.htwg.se.soccercardclash.model.cardComponent.factory.{CardFactory, ICardFactory}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardFactorySpec extends AnyWordSpec with Matchers {

  private val cardFactory: ICardFactory = new CardFactory()

  "CardFactory" should {

    "create a RegularCard with the correct suit and value" in {
      val card: ICard = cardFactory.createCard(Value.Ten, Suit.Hearts)

      card shouldBe a[RegularCard]
      card.value shouldBe Value.Ten
      card.suit shouldBe Suit.Hearts
    }

    "create multiple unique cards" in {
      val card1: ICard = cardFactory.createCard(Value.Ace, Suit.Spades)
      val card2: ICard = cardFactory.createCard(Value.King, Suit.Diamonds)

      card1 shouldBe a[RegularCard]
      card2 shouldBe a[RegularCard]
      card1 should not be card2
    }

    "ensure created card retains its properties" in {
      val card: ICard = cardFactory.createCard(Value.Queen, Suit.Clubs)

      card.value shouldBe Value.Queen
      card.suit shouldBe Suit.Clubs
      card.toString shouldBe "Queen of Clubs"
    }
  }
}