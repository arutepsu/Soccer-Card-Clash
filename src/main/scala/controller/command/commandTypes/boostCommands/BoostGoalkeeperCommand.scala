package controller.command.commandTypes.boostCommands

import controller.command.ICommand
import controller.command.base.Command
import model.playingFiledComponent.manager.ActionManager

class BoostGoalkeeperCommand(gc: ActionManager) extends Command(gc) {
  private var boostValue: Int = 0

  override protected def executeAction(): Unit = {
    //    val goalkeeperOpt = gc.getGoalkeeper(gc.pf.getAttacker)
    //
    //    goalkeeperOpt.foreach { goalkeeper =>
    //      boostValue = goalkeeper.getBoostingPolicies // ✅ Get and store boost value
    //      gc.boostGoalkeeper() // ✅ Delegate boosting logic to GameController
    //    }
  }
}