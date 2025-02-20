package controller.command.commandTypes.swapCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.ActionManager

class CircularSwapCommand(cardIndex: Int, gc: ActionManager) extends Command(gc) {

  override protected def executeAction(): Unit = {
    gc.circularSwap(cardIndex)
  }
}