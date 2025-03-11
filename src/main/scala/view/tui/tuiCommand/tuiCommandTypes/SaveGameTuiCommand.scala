package view.tui.tuiCommand.tuiCommandTypes

import controller.{Events, IController}
import view.tui.tuiCommand.base.ITuiCommand

class SaveGameTuiCommand(controller: IController) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    try {
      controller.saveGame()
      println(s"✅ Game saved successfully.")
    } catch {
      case e: Exception =>
        println(s"❌ ERROR: Failed to save game: ${e.getMessage}")
    }
  }
}

