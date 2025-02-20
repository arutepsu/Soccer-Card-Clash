package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
class CircularSwapStrategy extends SwapStrategy {
  override def swap(attackerHand: HandCardsQueue, index: Int): Unit = {
    if (attackerHand.getHandSize < 2) {
      return
    }

    if (index < 0 || index >= attackerHand.getHandSize) {
      return
    }

    val cardToMove = attackerHand.remove(index)
    attackerHand.enqueue(cardToMove)
  }
}
