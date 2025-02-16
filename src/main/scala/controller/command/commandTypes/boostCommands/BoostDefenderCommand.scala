package controller.command.commandTypes.boostCommands

import controller.command.base.BaseCommand
import model.cardComponent.base.Card
import model.playingFiledComponent.PlayingField
import util.ICommand

//class BoostDefenderCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
//  override protected def executeAction(): Unit = {
//    pf.chooseBoostCardDefender(cardIndex) // ✅ Boosts the defender
//  }
//
//  override def undoStep(): Unit = {
//    memento.foreach(m => restoreBoosts(m, cardIndex)) // ✅ Only undo the boosted card at `cardIndex`
//  }
//
//
//}
import controller.command.base.BaseCommand
import controller.gameBase.GameController
import model.cardComponent.base.Card
import model.playingFiledComponent.PlayingField
import util.ICommand

class BoostDefenderCommand(cardIndex: Int, gc: GameController) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    gc.boostDefender(cardIndex) // ✅ Delegate boosting logic to GameController
  }

  override def undoStep(): Unit = {
    memento.foreach(m => restoreBoosts(m, cardIndex)) // ✅ Only undo the boosted card at `cardIndex`
  }
}
