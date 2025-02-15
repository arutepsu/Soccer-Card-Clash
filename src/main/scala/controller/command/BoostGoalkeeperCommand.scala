package controller.command

import model.cardComponent.Card
import model.playingFiledComponent.PlayingField
import util.Command

class BoostGoalkeeperCommand(pf: PlayingField) extends BaseCommand(pf) {
  private var boostValue: Int = 0 // ✅ Track boost value separately for undo

  override protected def executeAction(): Unit = {
    val goalkeeperOpt = pf.getGoalkeeper(pf.getAttacker)

    goalkeeperOpt.foreach { goalkeeper =>
      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
      pf.chooseBoostCardGoalkeeper() // ✅ Apply the boost
    }
  }
}
