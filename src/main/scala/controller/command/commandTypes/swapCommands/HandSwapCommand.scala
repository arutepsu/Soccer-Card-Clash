package controller.command.commandTypes.swapCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.ActionManager

class HandSwapCommand(cardIndex: Int, actionManager: ActionManager) extends Command(actionManager) {

  override protected def executeAction(): Unit = {
    actionManager.handSwap(cardIndex)
  }
}