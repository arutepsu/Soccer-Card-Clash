package model.cardComponent

import scala.collection.mutable
import scala.util.Random

object Deck {
  def createDeck(): mutable.Queue[Card] = {
    val deck = for {
      suit <- Suit.values.toList
      value <- Value.allValues
    } yield Card(value, suit)
    mutable.Queue(deck: _*) // Convert the list to a mutable Queue
  }

  def shuffleDeck(deck: mutable.Queue[Card]): Unit = {
    val shuffled = Random.shuffle(deck)
    deck.clear()
    deck.enqueueAll(shuffled)
  }
}
