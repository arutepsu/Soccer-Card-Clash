package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.view.tui.IPrompter
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.factory.ITuiCommandFactory

class ShowAvailableGamesTuiCommand(controller: IController, prompter: IPrompter, factory: ITuiCommandFactory) extends ITuiCommand {
  override def execute(input: Option[String]): Unit = {
    input match {
      case Some(value) if value.startsWith("select") =>
        val parts = value.split(" ")
        if (parts.length == 2 && parts(1).forall(_.isDigit)) {
          val index = parts(1).toInt - 1
          prompter.loadSelectedGame(index, factory)
        } else {
          println("❌ Usage: select <number>")
        }

      case _ =>
        if (controller.getCurrentGame != null) {
          println("ℹ️ A game is already loaded. This will not affect the current session.")
        }

        println("🎮 Showing available saved games:")
        prompter.showAvailableGames()
    }
  }
}
