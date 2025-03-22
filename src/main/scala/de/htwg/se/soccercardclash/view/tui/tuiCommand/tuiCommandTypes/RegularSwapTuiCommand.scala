package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class RegularSwapTuiCommand(controller: IController) extends ITuiCommand {
  private var waitingForIndex: Boolean = false
  
  override def execute(input: Option[String]): Unit = {
    val attacker = controller.getCurrentGame.getPlayingField.getAttacker
    val handCards = controller.getCurrentGame.getPlayingField.getDataManager.getPlayerHand(attacker)

    if (handCards.isEmpty) {
      println("❌ No cards available to swap!")
    } else {
      println(s"🔄 ${attacker.name}'s hand cards:")
      handCards.zipWithIndex.foreach { case (card, index) =>
        println(s"[$index] ${card}")
      }

      val lastCard = handCards.last
      println(s"Choose a card you want to swap with (Last card: $lastCard). Enter index:")

      waitingForIndex = true
    }
  }
  
  def handleSwapInput(input: String): Unit = {
    if (!waitingForIndex) return

    val attacker = controller.getCurrentGame.getPlayingField.getAttacker
    val handCards = controller.getCurrentGame.getPlayingField.getDataManager.getPlayerHand(attacker)

    try {
      val position = input.toInt
      if (position >= 0 && position < handCards.size) {
        println(s"✅ Swapping card at position: $position with last card (${handCards.last})")
        controller.regularSwap(position)
        println("✅ Cards are swapped!")
      } else {
        println("❌ Invalid index! Choose a valid hand card index.")
      }
    } catch {
      case _: NumberFormatException =>
        println("❌ Error: Invalid input! Enter a number corresponding to the card index.")
    }

    waitingForIndex = false
  }
}
