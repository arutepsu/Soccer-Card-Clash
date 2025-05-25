package de.htwg.se.soccercardclash.model.cardComponent.dataStructure

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import play.api.libs.json.*

import scala.util.{Failure, Success, Try}
import scala.collection.mutable
import scala.xml.*

trait IHandCardsQueue {
  def cards: List[ICard]

  def addCard(card: ICard): IHandCardsQueue

  def removeLastCard(): Try[(ICard, IHandCardsQueue)]

  def splitAtEnd(n: Int): (List[ICard], IHandCardsQueue)

  def swap(index1: Int, index2: Int): Try[IHandCardsQueue]

  def getHandSize: Int = cards.size

  def toList: List[ICard] = cards

  def toXml: Elem =
    <HandCardsQueue>{cards.map(_.toXml)}</HandCardsQueue>

  def toJson: JsObject = Json.obj(
    "cards" -> cards.map(_.toJson)
  )
}


case class HandCardsQueue(cards: List[ICard]) extends IHandCardsQueue {

  override def addCard(card: ICard): IHandCardsQueue =
    HandCardsQueue(card :: cards)

  override def removeLastCard(): Try[(ICard, IHandCardsQueue)] =
    cards.reverse match {
      case last :: rest => Success((last, HandCardsQueue(rest.reverse)))
      case Nil => Failure(new NoSuchElementException("Hand is empty!"))
    }

  override def splitAtEnd(n: Int): (List[ICard], IHandCardsQueue) = {
    val (remaining, taken) = cards.splitAt(cards.length - n max 0)
    (taken, HandCardsQueue(remaining))
  }
  override def swap(index1: Int, index2: Int): Try[IHandCardsQueue] = Try {
    if (index1 < 0 || index2 < 0 || index1 >= cards.length || index2 >= cards.length)
      throw new IndexOutOfBoundsException("Invalid indices for swap.")

    val swapped = cards
      .updated(index1, cards(index2))
      .updated(index2, cards(index1))

    HandCardsQueue(swapped)
  }

}