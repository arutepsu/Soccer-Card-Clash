package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import controller.gameBase.GameManager
import model.playingFiledComponent.PlayingField

class SingleAttackCommand(defenderIndex: Int, gc: GameManager) extends BaseCommand(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.attack(defenderIndex) // ✅ Delegate to GameController
  }
}