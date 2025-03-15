package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame

class HandSwapActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var swapSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    swapSuccessful = Some(actionManager.handSwap(cardIndex))
    swapSuccessful.getOrElse(false)
  }
}
