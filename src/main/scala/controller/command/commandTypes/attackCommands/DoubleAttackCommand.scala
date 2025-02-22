package controller.command.commandTypes.attackCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}

import scala.collection.mutable

class DoubleAttackCommand(defenderIndex: Int, actionManager: IActionManager) extends Command(actionManager) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = actionManager.doubleAttack(defenderIndex)
  }
}
