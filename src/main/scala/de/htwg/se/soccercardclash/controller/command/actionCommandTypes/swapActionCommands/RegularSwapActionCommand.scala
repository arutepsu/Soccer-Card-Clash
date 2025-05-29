package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.util.{EventDispatcher, ObservableEvent}

import scala.util.{Failure, Success, Try}

class RegularSwapActionCommand(
                                cardIndex: Int,
                                actionManager: IActionManager
                              ) extends ActionCommand {

  override def executeAction(state: IGameState): Option[(IGameState, List[ObservableEvent])] = {
    Try(actionManager.regularSwap(state, cardIndex)) match {
      case Success((true, updatedState, events)) =>
        Some((updatedState, events))
      case _ =>
        None
    }
  }
}
