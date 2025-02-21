package model.cardComponent.cardFactory

import model.cardComponent.ICard
import model.cardComponent.cardFactory.CardFactory
import scala.collection.mutable
import scala.util.Random
import com.google.inject.{Inject, Singleton}
import model.cardComponent.base.components.{Suit, Value}

@Singleton
class DeckFactory @Inject() (cardFactory: ICardFactory) extends IDeckFactory {

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
