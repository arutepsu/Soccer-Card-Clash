package view.tui.tuiCommand.tuiCommandTypes

import controller.{Events, IController}
import view.tui.tuiCommand.base.ITuiCommand

class LoadGameTuiCommand(controller: IController, fileName: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    try {
      controller.loadGame(fileName)
      controller.notifyObservers(Events.PlayingField)
      println(s"✅ Game '$fileName' loaded successfully.")
    } catch {
      case e: Exception =>
        println(s"❌ ERROR: Failed to load game '$fileName': ${e.getMessage}")
    }
  }
}
