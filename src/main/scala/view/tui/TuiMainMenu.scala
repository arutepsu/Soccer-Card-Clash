package view.tui

import controller.IController
import scala.io.StdIn.readLine

class TuiMainMenu(manager: TuiManager, controller: IController) extends TuiBase {

  override def run(): Unit = {
    println(
      """|======== Main Menu ========
         |1. Create New Game
         |2. Load Game
         |3. Exit
         |===========================
         |Enter choice:""".stripMargin
    )

    readLine().trim match {
      case "1" => manager.switchTui(new TuiCreatePlayer(manager, controller))
      case "2" => manager.switchTui(new TuiLoadGame(manager, controller))
      case "3" => println("👋 Exiting..."); System.exit(0)
      case _   => println("❌ Invalid choice."); run()
    }
  }
}
