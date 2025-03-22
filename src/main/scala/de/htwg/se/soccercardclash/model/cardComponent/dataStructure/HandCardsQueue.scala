package de.htwg.se.soccercardclash.model.cardComponent.dataStructure

import de.htwg.se.soccercardclash.model.cardComponent.ICard

import play.api.libs.json.*
import scala.util.{Try, Success, Failure}
import scala.collection.mutable
import scala.xml.*

class HandCardsQueue(initialCards: List[ICard]) extends IHandCardsQueue {

  this.enqueueAll(initialCards)

  override def getCards: mutable.Queue[ICard] = this

  override def addCard(card: ICard): Unit = this.prepend(card)

  override def removeLastCard(): ICard = {
    Try {
      if (this.nonEmpty) {
        this.remove(this.size - 1)
      } else {
        throw new NoSuchElementException("Hand is empty!")
      }
    } match {
      case Success(card) => card
      case Failure(exception) =>
        throw new RuntimeException(s"❌ Error removing last card: ${exception.getMessage}", exception)
    }
  }

  override def getHandSize: Int = this.size
}

trait IHandCardsQueue extends mutable.Queue[ICard] {
  def getCards: mutable.Queue[ICard]

  def addCard(card: ICard): Unit

  def removeLastCard(): ICard

  def getHandSize: Int

  def toXml: Elem = {
    <HandCardsQueue>
      {getCards.map(_.toXml)}
    </HandCardsQueue>
  }

  def toJson: JsObject = Json.obj(
    "cards" -> getCards.map(_.toJson)
  )
}