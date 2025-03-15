package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame

class HandSwapActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var swapSuccessful: Option[Boolean] = None // Declare swapSuccessful

  override protected def executeAction(): Boolean = {
    swapSuccessful = Some(actionManager.handSwap(cardIndex)) // Assign result
    swapSuccessful.getOrElse(false) // Ensure a Boolean is returned
  }
}
