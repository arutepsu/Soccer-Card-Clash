package controller.command.actionCommandTypes.boostActionCommands

import controller.Events
import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager

import scala.util.{Failure, Success, Try}

class BoostDefenderActionCommand(cardIndex: Int, game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var boostSuccessful: Option[Boolean] = None

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

  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.boostDefender(cardIndex))
    result match {
      case Success(value) =>
        boostSuccessful = Some(value)
        value
      case Failure(exception) =>
        boostSuccessful = Some(false)
        false
    }
  }
}
