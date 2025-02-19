package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
trait SwapStrategy {
  def swap(attackerHand: HandCardsQueue, index: Int): Unit
}
