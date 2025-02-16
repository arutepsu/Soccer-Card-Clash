package controller.command.commandTypes

import controller.command.baseCommandImplementation.BaseCommand
import model.cardComponent.cardBaseImplementation.Card
import model.playingFiledComponent.PlayingField
import util.ICommand

class BoostDefenderCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  override protected def executeAction(): Unit = {
    pf.chooseBoostCardDefender(cardIndex) // ✅ Boosts the defender
  }

  override def undoStep(): Unit = {
    memento.foreach(m => restoreBoosts(m, cardIndex)) // ✅ Only undo the boosted card at `cardIndex`
  }


}
