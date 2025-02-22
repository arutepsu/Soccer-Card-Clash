package view.tui
import controller.{Events, IController}

class StartGameCommand(controller: IController) extends TuiCommand {
  private var waitingForNames: Boolean = false

  /** ✅ Step 1: Start Game and Ask for Player Names */
  override def execute(input: Option[String] = None): Unit = {
    println("🎮 Starting a new game! Enter player names (format: `player1 player2`):")
    waitingForNames = true
    controller.notifyObservers(Events.CreatePlayers)
  }

  /** ✅ Step 2: Handle Player Name Input */
  def handlePlayerNames(input: String): Boolean = {
    if (!waitingForNames) return false // Ignore if not waiting for input

    val playerNames = input.split(" ").map(_.trim).filter(_.nonEmpty)
    if (playerNames.length == 2) {
      val player1 = playerNames(0)
      val player2 = playerNames(1)

      println(s"✅ Starting game with players: $player1 & $player2") // Debugging print
      controller.startGame(player1, player2)
//      controller.notifyObservers(Events.PlayingField)
      waitingForNames = false // Reset state
      return true
    } else {
      println("❌ Invalid format! Enter names in the format: `player1 player2`.")
    }
    false
  }
}
