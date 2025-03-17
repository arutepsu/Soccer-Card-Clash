package controller.command.actionCommandTypes.boostActionCommands

import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.playingFiledComponent.manager.IActionManager
import model.gameComponent.IGame
import scala.util.{Try, Success, Failure}
class BoostGoalkeeperActionCommand(game: IGame) extends ActionCommand(game) {
  private val actionManager: IActionManager = game.getActionManager
  private var boostSuccessful: Option[Boolean] = None

  override protected def executeAction(): Boolean = {
    val result = Try(actionManager.boostGoalkeeper())
    result match {
      case Success(value) =>
        boostSuccessful = Some(value)
        value
      case Failure(exception) =>
        boostSuccessful = Some(false)
        false
    }
  }

  override def undoStep(): Unit = {
    memento.foreach(m => {
      mementoManager.restoreGoalkeeperBoost(m)
    })
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
