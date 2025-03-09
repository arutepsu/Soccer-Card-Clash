package model.playingFiledComponent.dataStructure

import model.cardComponent.ICard
import play.api.libs.json._
import scala.xml._
import scala.collection.mutable

class HandCardsQueue(initialCards: List[ICard]) extends IHandCardsQueue{

  this.enqueueAll(initialCards)

  override def getCards: mutable.Queue[ICard] = this

  override def addCard(card: ICard): Unit = this.prepend(card)

  override def removeLastCard(): ICard = {
    if (this.nonEmpty) {
      this.remove(this.size - 1)
    } else {
      throw new NoSuchElementException("Hand is empty!")
    }
  }

  override def getHandSize: Int = this.size
}
trait IHandCardsQueue extends mutable.Queue[ICard]  {
  def getCards: mutable.Queue[ICard]
  def addCard(card: ICard): Unit
  def removeLastCard(): ICard
  def getHandSize: Int

  def toXml: Elem = {
    <HandCardsQueue>
      {getCards.map(_.toXml)}
    </HandCardsQueue>
  }

  // ✅ JSON Serialization
  def toJson: JsObject = Json.obj(
    "cards" -> getCards.map(_.toJson)
  )
}