package controller.command.commandTypes.boostCommands

import controller.command.ICommand
import controller.command.base.Command
import model.playingFiledComponent.manager.IActionManager

class BoostDefenderCommand(cardIndex: Int, actionManager: IActionManager) extends Command(actionManager) {

  override protected def executeAction(): Unit = {
    actionManager.boostDefender(cardIndex)
  }

  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
  }
}