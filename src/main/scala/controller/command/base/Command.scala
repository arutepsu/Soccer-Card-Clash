package controller.command.base

import controller.command.ICommand
import controller.command.memento.*
import model.playingFiledComponent.manager.base.ActionManager

abstract class Command(val gameController: ActionManager) extends ICommand {
  protected val mementoManager = new MementoManager(gameController)
  protected var memento: Option[Memento] = None

  override def doStep(): Unit = {
    memento = Some(mementoManager.createMemento())
    executeAction()
  }

  override def undoStep(): Unit = {
    memento.foreach(mementoManager.restoreGameState)
  }

  override def redoStep(): Unit = {
    doStep()
  }

  protected def executeAction(): Unit

}