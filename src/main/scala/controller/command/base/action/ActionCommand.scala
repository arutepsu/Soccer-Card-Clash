package controller.command.base.action

import controller.command.ICommand
import controller.command.memento.*
import controller.command.memento.base.{Memento, MementoManager}
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import controller.command.memento.IMementoManager
import controller.command.memento.factory.IMementoManagerFactory

abstract class ActionCommand(
                              val game: IGame,
                              mementoManagerFactory: IMementoManagerFactory
                            ) extends ICommand {

  protected val actionManager: IActionManager = game.getActionManager
  protected var mementoManager: IMementoManager = mementoManagerFactory.create(game)
  protected var memento: Option[Memento] = None

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

  protected def executeAction(): Boolean
}
