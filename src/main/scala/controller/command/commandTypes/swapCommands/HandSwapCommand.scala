package controller.command.commandTypes.swapCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.PlayingField
class HandSwapCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {

  override protected def executeAction(): Unit = {
    // ✅ Ensure the strategy is set to HandSwap before execution
    pf.setSwapStrategy("hand")
    pf.swapAttacker(cardIndex)
  }
}

