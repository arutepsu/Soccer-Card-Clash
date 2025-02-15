package controller.command
import model.cardComponent.Card
import model.playingFiledComponent.PlayingField
import util.Command

class BoostDefenderCommand(cardIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  override protected def executeAction(): Unit = {
    pf.chooseBoostCardDefender(cardIndex) // ✅ Boosts the defender
  }
}
