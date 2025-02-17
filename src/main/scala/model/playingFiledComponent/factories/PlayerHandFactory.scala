package model.playingFiledComponent.factories
import model.cardComponent.base.Card
import scala.collection.mutable

object PlayerHandFactory {
  def createHand(initialCards: List[Card]): mutable.Queue[Card] = {
    mutable.Queue(initialCards: _*)
  }
}
