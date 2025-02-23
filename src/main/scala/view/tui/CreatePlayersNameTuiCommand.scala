package view.tui
import controller.{Events, IController}

class CreatePlayersNameTuiCommand(controller: IController) extends ITuiCommand {
  private var waitingForNames: Boolean = false

  override def execute(input: Option[String] = None): Unit = {
    waitingForNames = true
    controller.notifyObservers(Events.CreatePlayers)
  }

  def handlePlayerNames(input: String): Boolean = {
    if (!waitingForNames) return false

    val playerNames = input.split(" ").map(_.trim).filter(_.nonEmpty)
    if (playerNames.length == 2) {
      val player1 = playerNames(0)
      val player2 = playerNames(1)

      println(s"✅ Players set: $player1 & $player2") // Debugging print
      controller.notifyObservers(Events.CreatePlayers)

      // Execute StartGameCommand with the collected names
      val startGameCommand = new StartGameTuiCommand(controller, player1, player2)
      startGameCommand.execute()

      waitingForNames = false // Reset state
      return true
    } else {
      println("❌ Invalid format! Enter names in the format: `player1 player2`.")
    }
    false
  }
}
