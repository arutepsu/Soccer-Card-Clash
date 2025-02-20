package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable

class HandSwapStrategy extends SwapStrategy {
  override def swap(attackerHand: HandCardsQueue, index: Int): Unit = {
    if (attackerHand.getHandSize < 2) {
      return
    }

    if (index < 0 || index >= attackerHand.getHandSize) {
      return
    }

    val lastIndex = attackerHand.getHandSize - 1
    val chosenCard = attackerHand(index)
    val lastCard = attackerHand(lastIndex)

    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)
    
  }
}
