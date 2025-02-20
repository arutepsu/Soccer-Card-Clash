package controller.command.commandTypes.swapCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.manager.ActionManager
class HandSwapCommand(cardIndex: Int, actionManager: ActionManager) extends BaseCommand(actionManager) {

  override protected def executeAction(): Unit = {
    actionManager.handSwap(cardIndex)
  }
}