package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager

import scala.util.{Failure, Success, Try}

class ReverseSwapActionCommand(game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var swapSuccessful: Option[Boolean] = None

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
