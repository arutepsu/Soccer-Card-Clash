package model.playingFiledComponent.strategy.swapStrategy
import scala.collection.mutable
import model.cardComponent.base.Card

class HandSwapStrategy extends SwapStrategy {
  override def swap(attackerHand: mutable.Queue[Card], index: Int): Unit = {
    if (attackerHand.size < 2) {
      println("⚠️ Not enough cards to swap. No swap performed.")
      return
    }

    if (index < 0 || index >= attackerHand.size) {
      println(s"⚠️ Invalid swap index: $index. No swap performed.")
      return
    }

    val lastIndex = attackerHand.size - 1
    val chosenCard = attackerHand(index)
    val lastCard = attackerHand(lastIndex)

    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)

    println(s"🔄 Swapped Attacker's Cards: ${chosenCard} <--> ${lastCard}")
  }
}
