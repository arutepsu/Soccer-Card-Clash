package controller.command.commandTypes.boostCommands

import controller.command.base.BaseCommand
import controller.gameBase.GameController
import model.cardComponent.base.Card
import model.playingFiledComponent.PlayingField
import util.ICommand
//class BoostGoalkeeperCommand(pf: PlayingField) extends BaseCommand(pf) {
//  private var boostValue: Int = 0 // ✅ Track boost value separately for undo
//
//  override protected def executeAction(): Unit = {
//    val goalkeeperOpt = pf.fieldState.getPlayerGoalkeeper(pf.getAttacker)
//
//    goalkeeperOpt.foreach { goalkeeper =>
//      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
//      pf.boostManager.chooseBoostCardGoalkeeper() // ✅ Apply the boost
//    }
//  }
//}
class BoostGoalkeeperCommand(gc: GameController) extends BaseCommand(gc) {
  private var boostValue: Int = 0 // ✅ Track boost value separately for undo

  override protected def executeAction(): Unit = {
//    val goalkeeperOpt = gc.getGoalkeeper(gc.pf.getAttacker)
//
//    goalkeeperOpt.foreach { goalkeeper =>
//      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
//      gc.boostGoalkeeper() // ✅ Delegate boosting logic to GameController
//    }
  }
}
