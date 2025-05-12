package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.IBoostManager
import de.htwg.se.soccercardclash.util.{EventDispatcher, Events, ObservableEvent}

import scala.util.{Failure, Success, Try}

class BoostDefenderActionCommand(
                                  cardIndex: Int,
                                  actionManager: IActionManager
                                ) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    Try(actionManager.boostDefender(state, cardIndex)) match {
      case Success((true, updatedState, events)) =>
        Some((updatedState, events))
      case _ =>
        None
    }
  }
}

