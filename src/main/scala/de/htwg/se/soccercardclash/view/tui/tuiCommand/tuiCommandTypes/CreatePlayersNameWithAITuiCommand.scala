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

    val playerName = input.split(" ").map(_.trim).filter(_.nonEmpty)
    if (playerName.length == 1) {
      val player1 = playerName(0)
      println(s"Players set: $player1 vs AI")

      val command = new StartGameTuiCommandWithAI(controller, contextHolder, player1)

      command.execute()
      waitingForName = false
      true
    } else {
      println("Invalid input! Please enter exactly one name (no spaces).")
      false
    }
  }
}
