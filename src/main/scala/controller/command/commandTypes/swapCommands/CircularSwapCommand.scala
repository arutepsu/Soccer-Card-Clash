package controller.command.commandTypes.swapCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.manager.ActionManager

class CircularSwapCommand(cardIndex: Int, gc: ActionManager) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    gc.circularSwap(cardIndex)
  }
}