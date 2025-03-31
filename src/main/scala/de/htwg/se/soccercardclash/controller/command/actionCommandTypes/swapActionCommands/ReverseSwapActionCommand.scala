package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}

import scala.util.{Failure, Success, Try}

class ReverseSwapActionCommand(game: IGame,  mementoManagerFactory: IMementoManagerFactory) extends ActionCommand(game, mementoManagerFactory) {
  private val actionManager: IActionManager = game.getActionManager
  protected var swapSuccessful: Option[Boolean] = None
  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.reverseSwap(actionManager.getPlayerActionService))
    result match {
      case Success(value) =>
        swapSuccessful = Some(value)
        value
      case Failure(exception) =>
        swapSuccessful = Some(false)
        false
    }
  }
}
