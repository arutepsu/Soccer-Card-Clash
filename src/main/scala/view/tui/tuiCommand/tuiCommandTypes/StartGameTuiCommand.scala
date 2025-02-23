package view.tui.tuiCommand.tuiCommandTypes

import controller.{Events, IController}
import view.tui.tuiCommand.base.ITuiCommand

class StartGameTuiCommand(controller: IController, player1: String, player2: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    println(s"🎮 Starting game with players: $player1 & $player2")
    controller.startGame(player1, player2)
    controller.notifyObservers(Events.PlayingField)
  }
}
