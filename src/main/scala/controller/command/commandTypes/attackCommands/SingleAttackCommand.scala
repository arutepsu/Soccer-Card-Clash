package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import model.playingFiledComponent.base.{ActionHandler, PlayingField}

class SingleAttackCommand(defenderIndex: Int, gc: ActionHandler) extends BaseCommand(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.attack(defenderIndex) // ✅ Delegate to GameController
  }
}