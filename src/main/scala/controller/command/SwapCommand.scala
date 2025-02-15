package controller.command

import model.cardComponent.Card
import model.playingFiledComponent.PlayingField
import util.Command
class SwapCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf){

  override protected def executeAction(): Unit = {
    // Execute the swap
    pf.swapAttacker(cardIndex)
  }
}
