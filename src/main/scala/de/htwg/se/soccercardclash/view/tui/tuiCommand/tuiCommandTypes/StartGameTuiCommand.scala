package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.contextHolder.IGameContextHolder
import de.htwg.se.soccercardclash.util.{EventDispatcher, GlobalObservable, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class StartGameTuiCommand(controller: IController, contextHolder: IGameContextHolder, player1: String, player2: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    println(s"Starting game with players: $player1 & $player2")
    controller.createGame(player1, player2)
    val players = Seq(
      contextHolder.get.state.getRoles.attacker,
      contextHolder.get.state.getRoles.defender
    )
    contextHolder.get
    PlayerAvatarRegistry.assignAvatarsInOrder(players)
    EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)
  }
}
