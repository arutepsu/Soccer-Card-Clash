package view.tui.tuiCommand.tuiCommandTypes

import controller.{Events, IController}
import view.tui.tuiCommand.base.ITuiCommand

class AttackTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    val attacker = controller.getCurrentGame.getPlayingField.getAttacker
    val defender = controller.getCurrentGame.getPlayingField.getDefender
    val attackingCard = controller.getCurrentGame.getPlayingField.getDataManager.getAttackingCard
    val defendersField = controller.getCurrentGame.getPlayingField.getDataManager.getPlayerField(defender)

    if (attackingCard == null) {
      println("⚠️ No attacking cards available!")
      return
    }

    if (defendersField.isEmpty) {
      println("⚠️ No defenders to attack!")
      return
    }
    
    println("\n🛡 Defender's Field Cards:")
    defendersField.zipWithIndex.foreach { case (card, index) =>
      println(s"[$index] $card")
    }
    
    print("\n💥 Choose a position to attack: ")
    val inputStr = scala.io.StdIn.readLine().trim

    try {
      val position = inputStr.toInt

      if (position < 0 || position >= defendersField.length) {
        println("❌ Error: Invalid position! Choose a valid index from the list.")
      } else {
        if (position == 3 && defendersField.exists(_ != null)) {
          println("⚠️ You must beat the defenders before attacking the goalkeeper!")
          return
        }

        println(s"⚔️ Executing attack on position: $position")
        controller.executeSingleAttackCommand(position)
        
        val result = attackingCard.valueToInt - defendersField(position).valueToInt
        println(s"\n📊 Result: ${attackingCard} vs ${defendersField(position)}")
        println(s"🏆 Winner: ${if (result > 0) "Attacker" else if (result < 0) "Defender" else "Draw"}")
      }
    } catch {
      case _: NumberFormatException =>
        println("❌ Error: Invalid input! Expected a number representing the defender's position.")
    }
  }
}
