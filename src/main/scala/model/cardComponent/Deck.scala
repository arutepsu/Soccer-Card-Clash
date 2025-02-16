package model.cardComponent

import scala.collection.mutable
import scala.util.Random

object Deck {
  def createDeck(): mutable.Queue[Card] = {
    val deck = for {
      suit <- Suit.values.toList
      value <- Value.allValues
    } yield Card(value, suit)
    mutable.Queue(deck: _*)
  }

  def shuffleDeck(deck: mutable.Queue[Card]): Unit = {
    val shuffled = Random.shuffle(deck)
    deck.clear()
    deck.enqueueAll(shuffled)
  }

  def dealCards(): (List[Card], List[Card]) = {
    val deck = createDeck()
    shuffleDeck(deck)

    val hand1 = (1 to 26).map(_ => deck.dequeue()).toList
    val hand2 = (1 to 26).map(_ => deck.dequeue()).toList

    (hand1, hand2) // ✅ This should be (List[Card], List[Card])
  }

}
//package model.cardComponent
//import scala.collection.mutable
//import scala.util.Random
//object Deck {
//  /** ✅ Creates a full deck of 52 cards */
//  def createDeck(): mutable.Queue[Card] = {
//    val deck = for {
//      suit <- Suit.allSuits
//      value <- Value.allValues
//    } yield CardFactory.createCard(value, suit) // ✅ Use CardFactory
//    mutable.Queue(deck: _*)
//  }
//
//  /** ✅ Shuffles the deck */
//  def shuffleDeck(deck: mutable.Queue[Card]): Unit = {
//    val shuffled = Random.shuffle(deck)
//    deck.clear()
//    deck.enqueueAll(shuffled)
//  }
//}
