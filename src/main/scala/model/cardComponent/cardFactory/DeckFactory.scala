package model.cardComponent.cardFactory

import model.cardComponent.base.Card
import model.cardComponent.base.{Suit, Value}
import model.cardComponent.cardFactory.CardFactory
import scala.collection.mutable
import scala.util.Random

object DeckFactory {

def createDeck(): mutable.Queue[Card] = {
  val deck = for {
    suit <- Suit.allSuits
    value <- Value.allValues
  } yield CardFactory.createCard(value, suit)
  mutable.Queue(deck: _*)
}

  def shuffleDeck(deck: mutable.Queue[Card]): Unit = {
    val shuffled = Random.shuffle(deck)
    deck.clear()
    deck.enqueueAll(shuffled)
  }
}