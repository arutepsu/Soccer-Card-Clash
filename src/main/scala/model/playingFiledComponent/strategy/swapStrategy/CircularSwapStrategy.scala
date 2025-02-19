package model.playingFiledComponent.strategy.swapStrategy

import model.playingFiledComponent.dataStructure.HandCardsQueue
import scala.collection.mutable
class CircularSwapStrategy extends SwapStrategy {
  override def swap(attackerHand: HandCardsQueue, index: Int): Unit = {
    if (attackerHand.getHandSize < 2) {
      println("⚠️ Not enough cards to swap. No swap performed.")
      return
    }

    if (index < 0 || index >= attackerHand.getHandSize) {
      println(s"⚠️ Invalid swap index: $index. No swap performed.")
      return
    }

    val cardToMove = attackerHand.remove(index)
    attackerHand.enqueue(cardToMove) // Moves the selected card to the end

    println(s"🔄 Circular Swapped: Moved ${cardToMove} to the end of the attacker's hand.")
  }
}
