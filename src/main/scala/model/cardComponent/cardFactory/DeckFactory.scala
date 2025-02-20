package model.cardComponent.cardFactory

import model.cardComponent.ICard
import model.cardComponent.base.{Suit, Value}
import model.cardComponent.cardFactory.CardFactory

import scala.collection.mutable
import scala.util.Random

object DeckFactory {

  def createDeck(): mutable.Queue[ICard] = {
    val deck = for {
      suit <- Suit.allSuits
      value <- Value.allValues
    } yield CardFactory.createCard(value, suit)
    mutable.Queue(deck: _*)
  }

  def shuffleDeck(deck: mutable.Queue[ICard]): Unit = {
    val shuffled = Random.shuffle(deck)
    deck.clear()
    deck.enqueueAll(shuffled)
  }
}