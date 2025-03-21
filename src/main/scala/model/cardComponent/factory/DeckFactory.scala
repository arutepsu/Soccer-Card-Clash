package model.cardComponent.factory

import com.google.inject.{Inject, Singleton}
import model.cardComponent.ICard
import model.cardComponent.base.components.{Suit, Value}
import model.cardComponent.factory.CardFactory

import scala.collection.mutable
import scala.util.Random

@Singleton
class DeckFactory @Inject()(cardFactory: ICardFactory) extends IDeckFactory {

  override def createDeck(): mutable.Queue[ICard] = {
    val deck = for {
      suit <- Suit.allSuits
      value <- Value.allValues
    } yield cardFactory.createCard(value, suit)
    mutable.Queue(deck: _*)
  }

  override def shuffleDeck(deck: mutable.Queue[ICard]): Unit = {
    val shuffled = Random.shuffle(deck)
    deck.clear()
    deck.enqueueAll(shuffled)
  }
}

trait IDeckFactory {
  def createDeck(): mutable.Queue[ICard]

  def shuffleDeck(deck: mutable.Queue[ICard]): Unit
}
