package controller.command.commandTypes

import controller.command.baseCommandImplementation.BaseCommand
import model.cardComponent.cardBaseImplementation.Card
import model.playingFiledComponent.PlayingField
import util.ICommand

import scala.collection.mutable
//package controller
import model.playingFiledComponent.PlayingField
import util.ICommand
class SpecialAttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  override protected def executeAction(): Unit = {
    pf.attackHandler.executeDoubleAtack(defenderIndex) // ✅ Performs a stronger attack
  }
}
