package model.playingFiledComponent.strategy.swapStrategy
import scala.collection.mutable
import model.cardComponent.base.Card
import model.playingFiledComponent.state.HandCardsQueue
trait SwapStrategy {
  def swap(attackerHand: HandCardsQueue, index: Int): Unit
}
