package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IActionExecutor
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.attack.SingleAttackStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.IRevertBoostStrategyFactory
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class SingleAttackActionCommand(defenderIndex: Int,
                                actionExecutor: IActionExecutor,
                                revertBoostStrategyFactory: IRevertBoostStrategyFactory) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    val resultTry = Try {
      val revertBoostStrategy = revertBoostStrategyFactory.create(state)
      val strategy = SingleAttackStrategy(defenderIndex, revertBoostStrategy)
      actionExecutor.execute(strategy, state)
    }

    resultTry match {
      case Success((true, updatedState, events))  => Some((updatedState, events))
      case Success((false, _, _)) | Failure(_)    => None
    }
  }
}

