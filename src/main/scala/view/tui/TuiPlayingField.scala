package view.tui
import controller.IController
import scala.io.StdIn.readLine

class TuiPlayingField(manager: TuiManager, controller: IController) extends TuiBase {

  override def run(): Unit = {
    println("🎮 Game Started! Type 'attack', 'undo', 'redo', 'swap', or 'exit'.")

    var inGame = true
    while (inGame) {
      val input = readLine().trim.toLowerCase
      input match {
        case "attack" =>
          val defenderPosition = controller.selectDefenderPosition()
          if (defenderPosition == -1) {
            println("Attacking the goalkeeper.")
          } else if (defenderPosition == -2) {
            println("Select a defender position to attack:")
            val pos = readLine().toIntOption.getOrElse(-1)
            if (pos >= 1) controller.executeAttackCommand(pos - 1)
            else println("❌ Invalid input.")
          }

        case "undo" =>
          controller.undo()
          println("🔄 Undo executed!")

        case "redo" =>
          controller.redo()
          println("🔁 Redo executed!")

        case "swap" =>
          println("Select a card index to swap from attacker's hand:")
          val index = readLine().toIntOption.getOrElse(-1)
          if (index >= 0) {
            controller.swapAttackerCard(index)
            println(s"🔄 Swapped attacker card at index: $index")
          } else {
            println("❌ Invalid card index.")
          }

        case "exit" =>
          println("👋 Returning to Main Menu...")
          inGame = false
          manager.switchTui(new TuiMainMenu(manager, controller))

        case _ =>
          println("❌ Invalid command.")
      }
    }
  }
}
