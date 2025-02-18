package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import model.cardComponent.base.Card
import model.gameComponent.GameManager
import model.playingFiledComponent.PlayingField
import model.playingFiledComponent.strategy.attackStrategy.DoubleAttackStrategy
import model.playingFiledComponent.strategy.attackStrategy.SingleAttackStrategy
import scala.collection.mutable

class DoubleAttackCommand(defenderIndex: Int, gc: GameManager) extends BaseCommand(gc) {
  private var attackSuccessful: Boolean = false

  override protected def executeAction(): Unit = {
    attackSuccessful = gc.doubleAttack(defenderIndex) // ✅ Delegate to GameController
  }
}
