package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.manager.ActionManager

class SingleAttackCommand(defenderIndex: Int, gc: ActionManager) extends BaseCommand(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.attack(defenderIndex)
  }
}