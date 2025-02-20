package model.playingFiledComponent.dataStructure

import model.cardComponent.ICard

import scala.collection.mutable

class HandCardsQueue(initialCards: List[ICard]) extends mutable.Queue[ICard] {

  this.enqueueAll(initialCards)

  def getCards: mutable.Queue[ICard] = this

  def addCard(card: ICard): Unit = this.prepend(card)

  def removeLastCard(): ICard = {
    if (this.nonEmpty) {
      this.remove(this.size - 1)
    } else {
      throw new NoSuchElementException("Hand is empty!")
    }
  }

  def getHandSize: Int = this.size
}
