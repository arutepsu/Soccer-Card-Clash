package model.cardComponent.factory

import model.cardComponent.ICard
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.factory.{CardFactory, ICardFactory}
import model.cardComponent.factory.DeckFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class DeckFactorySpec extends AnyWordSpec with Matchers {

  private val cardFactory: ICardFactory = new CardFactory()
  private val deckFactory = new DeckFactory(cardFactory)

  "DeckFactory" should {

    "create a full deck of 52 unique cards" in {
      val deck = deckFactory.createDeck()

      deck should have size 52

      val uniqueCards = deck.map(card => (card.value, card.suit)).distinct
      uniqueCards should have size 52
    }

    "shuffle the deck and change card order" in {
      val deck = deckFactory.createDeck()
      val originalOrder = deck.toList

      deckFactory.shuffleDeck(deck)

      deck should have size 52
      deck.toList should not equal originalOrder // Most of the time — very rare edge case if same
    }

    "not lose or duplicate any cards after shuffle" in {
      val deck = deckFactory.createDeck()
      val originalSet = deck.map(card => (card.value, card.suit)).toSet

      deckFactory.shuffleDeck(deck)

      val shuffledSet = deck.map(card => (card.value, card.suit)).toSet
      shuffledSet shouldBe originalSet // Same cards after shuffle
    }
  }
}
