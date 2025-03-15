package controller.command.actionCommandTypes.boostActionCommands

import controller.Events
import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame

class BoostDefenderActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var boostSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    boostSuccessful = Some(actionManager.boostDefender(cardIndex))
    boostSuccessful.getOrElse(false)
  }

  override def undoStep(): Unit = {
    memento.foreach(m => mementoManager.restoreBoosts(m, cardIndex))
  }

  override def redoStep(): Unit = {
    memento match {
      case Some(savedMemento) if boostSuccessful.contains(true) =>
        mementoManager.restoreGameState(savedMemento)
        executeAction()
      case _ =>
    }
  }
}
