package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.base.DoubleAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.RevertBoostStrategy
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import scala.util.{Failure, Success, Try}


class DoubleAttackActionCommand(defenderIndex: Int,
                                actionManager: IActionManager) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    val playerActionManager = new PlayerActionManager()
    val revertBoostStrategy = new RevertBoostStrategy(state)
    val strategy = DoubleAttackStrategy(defenderIndex, playerActionManager, revertBoostStrategy)
    val (success, updatedState, events) = actionManager.execute(strategy, state)

    if success then Some((updatedState, events)) else None
  }
}
