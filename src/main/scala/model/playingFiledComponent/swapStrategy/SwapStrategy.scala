package model.playingFiledComponent.swapStrategy
import scala.collection.mutable
import model.cardComponent.base.Card
trait SwapStrategy {
  def swap(attackerHand: mutable.Queue[Card], index: Int): Unit
}
