package controller.command.commandTypes.swapCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.base.{ActionHandler, PlayingField}
//class HandSwapCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
//
//  override protected def executeAction(): Unit = {
//    // ✅ Ensure the strategy is set to HandSwap before execution
//    pf.setSwapStrategy("hand")
//    pf.swapAttacker(cardIndex)
//  }
//}

class HandSwapCommand(cardIndex: Int, gc: ActionHandler) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    // ✅ Delegate swap logic to GameController
    gc.handSwap(cardIndex)
  }
}