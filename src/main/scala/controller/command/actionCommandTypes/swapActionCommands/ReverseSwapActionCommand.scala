package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import controller.command.memento.factory.IMementoManagerFactory
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager

import scala.util.{Failure, Success, Try}

class ReverseSwapActionCommand(game: IGame,  mementoManagerFactory: IMementoManagerFactory) extends ActionCommand(game, mementoManagerFactory) {
  private val actionManager: IActionManager = game.getActionManager
  protected var swapSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.reverseSwap())
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
