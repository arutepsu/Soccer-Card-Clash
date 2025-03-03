package controller.command.actionCommandTypes.boostActionCommands

import controller.Events
import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame

class BoostDefenderActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  override protected def executeAction(): Unit = {
    actionManager.boostDefender(cardIndex)
  }

  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
    println(s"✅ After restore: ${game.getPlayingField.getDataManager.getPlayerDefenders(game.getPlayingField.getAttacker)}")
  }
}