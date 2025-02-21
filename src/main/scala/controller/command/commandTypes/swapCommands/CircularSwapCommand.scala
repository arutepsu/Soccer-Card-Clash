package controller.command.commandTypes.swapCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager

class CircularSwapCommand(cardIndex: Int, gc: IActionManager) extends Command(gc) {

  override protected def executeAction(): Unit = {
    gc.circularSwap(cardIndex)
  }
}