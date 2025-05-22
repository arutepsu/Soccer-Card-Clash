package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.*
import de.htwg.se.soccercardclash.util.{GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes.StartGameTuiCommand
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

import java.lang.invoke.SwitchPoint

class CreatePlayersNameTuiCommand(controller: IController, contextHolder: IGameContextHolder) extends ITuiCommand {
  private var waitingForNames: Boolean = false

  override def execute(input: Option[String] = None): Unit = {
    waitingForNames = true
    GlobalObservable.notifyObservers(SceneSwitchEvent.CreatePlayer)
    println("Please enter players names:")
  }

  def handlePlayerNames(input: String): Boolean = {
    if (!waitingForNames) return false

    val playerNames = input.split(" ").map(_.trim).filter(_.nonEmpty)
    if (playerNames.length == 2) {
      val player1 = playerNames(0)
      val player2 = playerNames(1)

      println(s"Players set: $player1 & $player2")
      controller.notifyObservers(SceneSwitchEvent.CreatePlayer)

      val startGameCommand = new StartGameTuiCommand(controller, contextHolder, player1, player2)
      startGameCommand.execute()

      waitingForNames = false
      return true
    } else {
      println("Invalid format! Enter names in the format: `player1 player2`.")
    }
    false
  }
}
