package controller.command.base

import controller.command.ICommand
import controller.command.memento.*
import controller.command.memento.base.{Memento, MementoManager}
import model.playingFiledComponent.manager.IActionManager

abstract class Command(val actionManager: IActionManager) extends ICommand {
  protected val mementoManager: IMementoManager = new MementoManager(actionManager)
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