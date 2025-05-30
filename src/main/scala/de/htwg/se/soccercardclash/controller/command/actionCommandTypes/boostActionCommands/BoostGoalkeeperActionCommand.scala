package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionManager, IPlayerActionManager, PlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.IBoostManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base.GoalkeeperBoostStrategy
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class BoostGoalkeeperActionCommand(actionManager: IActionManager) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    val playerActionManager = new PlayerActionManager()
    val strategy = GoalkeeperBoostStrategy(playerActionManager)
    val (success, updatedState, events) = actionManager.execute(strategy, state)

    if success then Some((updatedState, events)) else None
  }
}

