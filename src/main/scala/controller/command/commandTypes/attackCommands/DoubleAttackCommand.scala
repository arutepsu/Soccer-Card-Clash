package controller.command.commandTypes.attackCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.base.ActionManager
import model.playingFiledComponent.strategy.attackStrategy.{DoubleAttackStrategy, SingleAttackStrategy}

import scala.collection.mutable

class DoubleAttackCommand(defenderIndex: Int, gc: ActionManager) extends Command(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.doubleAttack(defenderIndex)
  }
}
