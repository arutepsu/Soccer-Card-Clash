package controller.command.base.action

import controller.command.ICommand
import controller.command.memento.*
import controller.command.memento.base.{Memento, MementoManager}
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager

abstract class ActionCommand(val game: IGame) extends ICommand {
  protected val actionManager: IActionManager = game.getActionManager
  protected val mementoManager: IMementoManager = new MementoManager(game)

  protected var memento: Option[Memento] = None

  override def undoStep(): Unit = {
    memento match {
      case Some(savedMemento) =>
        mementoManager.restoreGameState(savedMemento)
        game.updateGameState()
      case None =>
    }
  }

  override def redoStep(): Unit = {
    memento match {
      case Some(_) =>
        val success = doStep()
      case None =>
    }
  }

  override def doStep(): Boolean = {
    val preActionState = mementoManager.createMemento()

    if (executeAction()) {
      memento = Some(preActionState)
      game.updateGameState()
      true
    } else {
      false
    }
  }

  protected def executeAction(): Boolean
}
