package controller.command.commandTypes.swapCommands

import controller.command.base.BaseCommand
import model.gameComponent.GameManager
import model.playingFiledComponent.PlayingField
//class CircularSwapCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
//
//  override protected def executeAction(): Unit = {
//    // ✅ Ensure the strategy is set to CircularSwap before execution
//    pf.setSwapStrategy("circular")
//    pf.swapAttacker(cardIndex)
//  }
//}
//
class CircularSwapCommand(cardIndex: Int, gc: GameManager) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    // ✅ Delegate swap logic to GameController
    gc.circularSwap(cardIndex)
  }
}