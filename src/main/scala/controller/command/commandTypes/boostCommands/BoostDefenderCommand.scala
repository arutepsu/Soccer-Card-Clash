package controller.command.commandTypes.boostCommands

import controller.command.ICommand
import controller.command.base.BaseCommand
import model.playingFiledComponent.manager.ActionManager

class BoostDefenderCommand(cardIndex: Int, gc: ActionManager) extends BaseCommand(gc) {

  override protected def executeAction(): Unit = {
    gc.boostDefender(cardIndex)
  }

  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
  }
}