package controller.command
import model.cardComponent.Card
import model.playingFiledComponent.PlayingField
import util.Command

class BoostDefenderCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  override protected def executeAction(): Unit = {
    pf.chooseBoostCardDefender(cardIndex) // ✅ Boosts the defender
  }

  override def undoStep(): Unit = {
    memento.foreach(m => restoreBoosts(m, cardIndex)) // ✅ Only undo the boosted card at `cardIndex`
  }


}
