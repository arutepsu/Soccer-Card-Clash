package controller.command.commandTypes.attackCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager
import model.playingFiledComponent.strategy.attackStrategy.{DoubleAttackStrategy, SingleAttackStrategy}

import scala.collection.mutable

class DoubleAttackCommand(defenderIndex: Int, gc: IActionManager) extends Command(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.doubleAttack(defenderIndex)
  }
}
