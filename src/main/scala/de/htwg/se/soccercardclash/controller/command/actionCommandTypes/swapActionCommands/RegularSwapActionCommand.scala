package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionManager, IPlayerActionManager, PlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swap.RegularSwapStrategy
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class RegularSwapActionCommand(
                                cardIndex: Int,
                                actionManager: IActionManager
                              ) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    val playerActionManager = new PlayerActionManager()
    val strategy = RegularSwapStrategy(cardIndex, playerActionManager)
    val (success, updatedState, events) = actionManager.execute(strategy, state)

    if success then Some((updatedState, events)) else None
  }
}
