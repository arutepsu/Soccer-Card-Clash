package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{Events, IController}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class StartGameTuiCommand(controller: IController, player1: String, player2: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    println(s"🎮 Starting game with players: $player1 & $player2")
    controller.createGame(player1, player2)
    controller.notifyObservers(Events.PlayingField)
  }
}
