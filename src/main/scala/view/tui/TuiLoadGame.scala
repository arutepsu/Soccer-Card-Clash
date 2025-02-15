package view.tui
import controller.IController
import scala.io.StdIn.readLine
import scala.util.{Success, Failure}

class TuiLoadGame(manager: TuiManager, controller: IController) extends TuiBase {

  override def run(): Unit = {
    println("\n📂 Enter saved game file path:")
    val filePath = readLine().trim

    controller.loadGame(filePath) match {
      case Success(_) =>
        println("✅ Game loaded successfully!")
        manager.switchTui(new TuiPlayingField(manager, controller))
      case Failure(ex) =>
        println(s"❌ Failed to load game: ${ex.getMessage}")
        manager.switchTui(new TuiMainMenu(manager, controller))
    }
  }
}
