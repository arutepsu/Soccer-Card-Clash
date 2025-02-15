package model.playingFiledComponent

class SwapHandler(
                   playingField: PlayingField,
                   roles: PlayerRoles,
                 ) {
  def swapAttacker(index: Int): Unit = {
    val attackerHand = playingField.getHand(roles.attacker) // ✅ Get attacker's hand

    if (index < 0 || index >= attackerHand.size) { // ✅ Validate index
      println(s"⚠️ Invalid swap index: $index. No swap performed.")
      return
    }

    if (attackerHand.size < 2) { // ✅ Ensure at least two cards exist
      println(s"⚠️ Not enough cards to swap in hand. No swap performed.")
      return
    }

    val lastIndex = attackerHand.size - 1 // ✅ Get last card index
    val chosenCard = attackerHand(index) // ✅ Get chosen card
    val lastCard = attackerHand(lastIndex) // ✅ Get last card

    // ✅ Swap the two cards in the attacker's hand
    attackerHand.update(index, lastCard)
    attackerHand.update(lastIndex, chosenCard)

    println(s"🔄 Swapped Attacker's Cards: ${chosenCard} <--> ${lastCard}")
    
    playingField.notifyObservers()
  }
}