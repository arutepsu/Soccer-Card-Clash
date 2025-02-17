package model.playingFiledComponent.state
import model.cardComponent.base.Card
import scala.collection.mutable
import scala.collection.mutable

class HandCardsQueue(initialCards: List[Card]) extends mutable.Queue[Card] {
  this.enqueueAll(initialCards)
  def getCards: mutable.Queue[Card] = this
  def addCard(card: Card): Unit = this.prepend(card)

  def removeLastCard(): Card = {
    if (this.nonEmpty) {
      this.remove(this.size - 1)
    } else {
      throw new NoSuchElementException("Hand is empty!")
    }
  }

  def getHandSize: Int = this.size
}
