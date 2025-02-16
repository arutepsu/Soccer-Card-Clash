package controller.command.commandTypes.attackCommands

import controller.command.base.BaseCommand
import model.cardComponent.base.Card
import model.playingFiledComponent.PlayingField
import model.playingFiledComponent.attackStrategy.DoubleAttackStrategy
import model.playingFiledComponent.attackStrategy.SingleAttackStrategy
import scala.collection.mutable

class DoubleAttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  private var attackSuccessful: Boolean = false
  override protected def executeAction(): Unit = {
    pf.setAttackStrategy(new DoubleAttackStrategy())
    attackSuccessful = pf.attack(defenderIndex)
    pf.setAttackStrategy(new SingleAttackStrategy())
    
  }
}
