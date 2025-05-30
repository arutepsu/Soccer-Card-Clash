package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionExecutor, IPlayerActionManager, PlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.DefenderBoostStrategy
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class BoostDefenderActionCommand(
                                  cardIndex: Int,
                                  actionExecutor: IActionExecutor,
                                  playerActionManager: IPlayerActionManager
                                ) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    Try {
      val strategy = DefenderBoostStrategy(cardIndex, playerActionManager)
      actionExecutor.execute(strategy, state)
    } match {
      case Success((true, updatedState, events))  => Some((updatedState, events))
      case Success((false, _, _)) | Failure(_)    => None
    }
  }
}