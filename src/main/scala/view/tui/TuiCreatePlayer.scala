package view.tui

import controller.IController
import scala.io.StdIn.readLine

class TuiCreatePlayer(manager: TuiManager, controller: IController) extends TuiBase {

  override def run(): Unit = {
    println("\n👥 Enter Player Names (2 players required):")

    val playerNames = (1 to 2).map { i =>
      print(s"Player $i: ")
      readLine().trim
    }.filter(_.nonEmpty)

    if (playerNames.size != 2) {
      println("❌ Exactly 2 players are required!")
      manager.switchTui(new TuiMainMenu(manager, controller))
      return
    }

    // ✅ Assign names
    controller.setPlayerName(1, playerNames.head)
    controller.setPlayerName(2, playerNames(1))
    println(s"✅ Players set: ${playerNames.head} & ${playerNames(1)}")

    // ✅ Start game and switch to playing field
    controller.startGame()
    manager.switchTui(new TuiPlayingField(manager, controller))
  }
}
