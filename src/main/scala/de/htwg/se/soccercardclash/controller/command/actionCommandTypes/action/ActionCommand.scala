package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

abstract class ActionCommand extends ICommand {
  def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])]

  override def execute(state: IGameState): CommandResult = {
    executeAction(state) match {
      case Some((updatedState, events)) =>
        CommandResult(success = true, updatedState, events)
      case None =>
        CommandResult(success = false, state, Nil)
    }
  }
}



