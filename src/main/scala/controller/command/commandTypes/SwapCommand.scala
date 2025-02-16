package controller.command.commandTypes

import controller.command.baseCommandImplementation.BaseCommand
import model.cardComponent.cardBaseImplementation.Card
import model.playingFiledComponent.PlayingField
import util.ICommand
class SwapCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf){

  override protected def executeAction(): Unit = {
    // Execute the swap
    pf.swapHandler.swapAttacker(cardIndex)
  }
}
