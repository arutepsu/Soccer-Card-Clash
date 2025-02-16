package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import controller.gameBase.GameController
import model.playingFiledComponent.PlayingField
//class SingleAttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
//  private var attackSuccessful: Boolean = false
//  override protected def executeAction(): Unit = {
//    attackSuccessful = pf.attack(defenderIndex)
//  }
//}
class SingleAttackCommand(defenderIndex: Int, gc: GameController) extends BaseCommand(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.attack(defenderIndex) // ✅ Delegate to GameController
  }
}
