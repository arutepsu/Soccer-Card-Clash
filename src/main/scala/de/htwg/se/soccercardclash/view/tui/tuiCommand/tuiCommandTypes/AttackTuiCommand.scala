package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class AttackTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    val game = controller.getCurrentGame
    if (game == null) {
      println("⚠️ No game loaded! Please load a game first using ':load'.")
      return
    }

    val attacker = game.getPlayingField.getAttacker
    val defender = game.getPlayingField.getDefender
    val attackingCard = game.getPlayingField.getDataManager.getAttackingCard
    val defendersField = game.getPlayingField.getDataManager.getPlayerField(defender)

    if (attackingCard == null) {
      println("⚠️ No attacking cards available!")
      return
    }

    if (defendersField.isEmpty) {
      println("⚠️ No defenders to attack!")
      return
    }

    // Validate input
    input match {
      case Some(str) =>
        str.toIntOption match {
          case Some(position) if position >= 0 && position < defendersField.length =>
            if (position == 3 && defendersField.exists(_ != null)) {
              println("⚠️ You must beat the defenders before attacking the goalkeeper!")
              return
            }

            println(s"⚔️ Executing attack on position: $position")
            controller.executeSingleAttackCommand(position)

            val result = attackingCard.valueToInt - defendersField(position).valueToInt
            println(s"\n📊 Result: ${attackingCard} vs ${defendersField(position)}")
            println(s"🏆 Winner: ${if (result > 0) "Attacker" else if (result < 0) "Defender" else "Draw"}")

          case _ =>
            println("❌ Error: Invalid input! Expected a valid defender index.")
        }
      case None =>
        println("❌ No input provided for attack position.")
    }
  }
}


