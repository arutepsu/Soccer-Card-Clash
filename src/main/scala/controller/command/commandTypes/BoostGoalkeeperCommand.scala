package controller.command.commandTypes

import controller.command.baseCommandImplementation.BaseCommand
import model.cardComponent.cardBaseImplementation.Card
import model.playingFiledComponent.PlayingField
import util.ICommand

class BoostGoalkeeperCommand(pf: PlayingField) extends BaseCommand(pf) {
  private var boostValue: Int = 0 // ✅ Track boost value separately for undo

  override protected def executeAction(): Unit = {
    val goalkeeperOpt = pf.fieldState.getGoalkeeper(pf.getAttacker)

    goalkeeperOpt.foreach { goalkeeper =>
      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
      pf.boostManager.chooseBoostCardGoalkeeper() // ✅ Apply the boost
    }
  }
}
