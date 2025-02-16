package model.playingFiledComponent.swapStrategy
import scala.collection.mutable
import model.cardComponent.base.Card
class CircularSwapStrategy extends SwapStrategy {
  override def swap(attackerHand: mutable.Queue[Card], index: Int): Unit = {
    if (attackerHand.size < 2) {
      println("⚠️ Not enough cards to swap. No swap performed.")
      return
    }

    if (index < 0 || index >= attackerHand.size) {
      println(s"⚠️ Invalid swap index: $index. No swap performed.")
      return
    }

    val cardToMove = attackerHand.remove(index)
    attackerHand.enqueue(cardToMove) // Moves the selected card to the end

    println(s"🔄 Circular Swapped: Moved ${cardToMove} to the end of the attacker's hand.")
  }
}
