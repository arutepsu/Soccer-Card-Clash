package controller.command.commandTypes

import controller.command.baseCommandImplementation.BaseCommand
import model.playingFiledComponent.PlayingField
import util.ICommand

import scala.collection.mutable


class AttackCommand(defenderIndex: Int, pf: PlayingField) extends BaseCommand(pf) {
  private var attackSuccessful: Boolean = false // Tracks if the attack was successful
  override protected def executeAction(): Unit = {
    attackSuccessful = pf.attack(defenderIndex) // ✅ Performs the attack action
  }
}
