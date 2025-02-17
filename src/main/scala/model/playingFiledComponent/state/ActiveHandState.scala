package model.playingFiledComponent.state

class ActiveHandState(initialCards: List[Card]) extends HandState {
  private val cards: mutable.Queue[Card] = mutable.Queue(initialCards: _*)

  override def getCards: mutable.Queue[Card] = cards
  override def addCard(card: Card): Unit = cards.enqueue(card)
  override def removeCard(card: Card): Boolean = cards.dequeueFirst(_ == card).isDefined
  override def isModifiable: Boolean = true
}
