package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.util.{GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.StartGameTuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class CreatePlayersNameWithAITuiCommand(
                                         controller: IController,
                                         contextHolder: IGameContextHolder
                                       ) extends ITuiCommand {

  private var waitingForName: Boolean = false

  override def execute(input: Option[String] = None): Unit = {
    waitingForName = true
    GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayerWithAI)
    println("Please enter your name (you will play against the AI):")
  }

  def handlePlayerNames(input: String): Boolean = {
    if (!waitingForName) return false

    val trimmed = input.trim
    if (trimmed.nonEmpty && !trimmed.contains(" ")) {
      val player1 = trimmed
      println(s"Players set: $player1 vs AI")

      // Start the game
      new StartGameTuiCommandWithAI(controller, contextHolder, player1).execute()

      waitingForName = false
      true
    } else {
      println("Invalid input! Please enter exactly one name (no spaces).")
      false
    }
  }
}
