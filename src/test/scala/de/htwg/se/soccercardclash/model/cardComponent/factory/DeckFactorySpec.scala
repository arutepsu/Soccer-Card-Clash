package de.htwg.se.soccercardclash.model.cardComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.components.{Suit, Value}
import de.htwg.se.soccercardclash.model.cardComponent.factory.{CardFactory, ICardFactory}
import de.htwg.se.soccercardclash.model.cardComponent.factory.DeckFactory
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
      deck.toList should not equal originalOrder
    }

    "not lose or duplicate any cards after shuffle" in {
      val deck = deckFactory.createDeck()
      val originalSet = deck.map(card => (card.value, card.suit)).toSet

      deckFactory.shuffleDeck(deck)

      val shuffledSet = deck.map(card => (card.value, card.suit)).toSet
      shuffledSet shouldBe originalSet
    }
  }
}