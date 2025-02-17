package controller.command.commandTypes.boostCommands

import controller.command.ICommand
import controller.command.base.BaseCommand
import controller.gameBase.GameController
import model.cardComponent.base.Card
import model.playingFiledComponent.PlayingField

class BoostDefenderCommand(cardIndex: Int, gc: GameController) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    gc.boostDefender(cardIndex)
  }

  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
  }
}