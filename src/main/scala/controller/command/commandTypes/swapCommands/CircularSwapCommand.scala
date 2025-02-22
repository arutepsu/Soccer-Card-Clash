package controller.command.commandTypes.swapCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager

class CircularSwapCommand(cardIndex: Int, actionManager: IActionManager) extends Command(actionManager) {

  override protected def executeAction(): Unit = {
    actionManager.circularSwap(cardIndex)
  }
}