package model.playerComponent.playerFactory

import model.cardComponent.ICard

import scala.collection.mutable

object PlayerHandFactory {
  def createHand(initialCards: List[ICard]): mutable.Queue[ICard] = {
    mutable.Queue(initialCards: _*)
  }
}
