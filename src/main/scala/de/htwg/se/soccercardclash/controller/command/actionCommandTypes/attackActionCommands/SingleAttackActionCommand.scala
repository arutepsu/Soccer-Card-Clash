package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class SingleAttackActionCommand(defenderIndex: Int,
                                actionManager: IActionManager) extends ActionCommand {
  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    Try(actionManager.singleAttack(state, defenderIndex)) match {
      case Success((true, updatedState, events)) =>
        Some((updatedState, events))
      case _ =>
        None
    }
  }
}

