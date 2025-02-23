package view.tui
import controller.{Events, IController}

class StartGameTuiCommand(controller: IController, player1: String, player2: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    println(s"🎮 Starting game with players: $player1 & $player2")
    controller.startGame(player1, player2)
    controller.notifyObservers(Events.PlayingField)
  }
}
