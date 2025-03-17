package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame
import scala.util.{Try, Success, Failure}

class HandSwapActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var swapSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.handSwap(cardIndex))
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
