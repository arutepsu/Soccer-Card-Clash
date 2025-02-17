package model.playingFiledComponent.state
import scala.collection.mutable
import model.cardComponent.base.Card

/** ✅ State Pattern for Player Hands */
trait HandState {
  def getCards: mutable.Queue[Card]
  def addCard(card: Card): Unit
  def removeCard(card: Card): Boolean
  def isModifiable: Boolean // Indicates if cards can be added/removed
}
