package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

import scala.util.{Failure, Success, Try}

class SaveGameTuiCommand(controller: IController, contextHolder: IGameContextHolder) extends ITuiCommand {

  override def execute(input: Option[String] = None): Unit = {
    Try {
      controller.saveGame(contextHolder.get)
    } match {
      case Success(_) =>
        println("Game saved successfully.")
      case Failure(e) =>
        println(s"ERROR: Failed to save game: ${e.getMessage}")
    }
  }
}