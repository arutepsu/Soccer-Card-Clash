package de.htwg.se.soccercardclash.view.tui.tuiCommand.tuiCommandTypes

import de.htwg.se.soccercardclash.controller.{IController, IGameContextHolder}
import de.htwg.se.soccercardclash.util.{EventDispatcher, SceneSwitchEvent}
import de.htwg.se.soccercardclash.view.gui.components.playerView.PlayerAvatarRegistry
import de.htwg.se.soccercardclash.view.tui.tuiCommand.base.ITuiCommand

class StartGameTuiCommandWithAI(controller: IController,
                                contextHolder: IGameContextHolder, 
                                player1: String) extends ITuiCommand {
  override def execute(input: Option[String] = None): Unit = {
    println(s"🎮 Starting game with players: $player1 & AI")
//    controller.createGameWithAI(player1)


    val players = Seq(
      contextHolder.get.state.getRoles.attacker,
      contextHolder.get.state.getRoles.defender
    )

    PlayerAvatarRegistry.assignAvatarsInOrder(players)
    EventDispatcher.dispatchSingle(controller, SceneSwitchEvent.PlayingField)
  }
}