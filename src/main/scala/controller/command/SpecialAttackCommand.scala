package controller.command

import model.cardComponent.Card
import model.playingFiledComponent.PlayingField
import util.Command

import scala.collection.mutable
//package controller
import model.playingFiledComponent.PlayingField
import util.Command
class SpecialAttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  override protected def executeAction(): Unit = {
    pf.doubleAtack(defenderIndex) // ✅ Performs a stronger attack
  }
}
