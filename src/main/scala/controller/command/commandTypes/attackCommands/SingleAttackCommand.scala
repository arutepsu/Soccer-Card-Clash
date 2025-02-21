package controller.command.commandTypes.attackCommands

import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager

class SingleAttackCommand(defenderIndex: Int, gc: IActionManager) extends Command(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.attack(defenderIndex)
  }
}