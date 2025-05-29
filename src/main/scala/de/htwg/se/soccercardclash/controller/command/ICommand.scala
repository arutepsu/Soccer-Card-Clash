package de.htwg.se.soccercardclash.controller.command

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.util.ObservableEvent

trait ICommand {
  def execute(state: IGameState): CommandResult
}

case class CommandResult(
                          success: Boolean,
                          state: IGameState,
                          events: List[ObservableEvent]
                        )
