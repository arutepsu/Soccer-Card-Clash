package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.SingleAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.RevertBoostStrategy
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class SingleAttackActionCommand(defenderIndex: Int,
                                actionManager: IActionManager) extends ActionCommand {
  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    val revertBoostStrategy = new RevertBoostStrategy(state)
    val strategy = SingleAttackStrategy(defenderIndex, revertBoostStrategy)
    val (success, updatedState, events) = actionManager.execute(strategy, state)

    if success then Some((updatedState, events)) else None
  }
}

