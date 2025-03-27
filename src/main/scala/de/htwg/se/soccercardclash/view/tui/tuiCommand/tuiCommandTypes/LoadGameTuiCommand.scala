package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.util.Events
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import scala.util.{Try, Success, Failure}

class LoadGameTuiCommand(controller: IController, fileName: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    Try {
      controller.loadGame(fileName)
      controller.notifyObservers(Events.PlayingField)
    } match {
      case Success(_) =>
        println(s"✅ Game '$fileName' loaded successfully.")
      case Failure(e) =>
        println(s"❌ ERROR: Failed to load game '$fileName': ${e.getMessage}")
    }
  }
}

