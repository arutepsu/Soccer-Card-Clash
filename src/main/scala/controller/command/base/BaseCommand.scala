package controller.command.base

import controller.command.ICommand
import model.playingFiledComponent.PlayingField
import controller.command.memento._
import model.gameComponent.GameManager

abstract class BaseCommand(val gameController: GameManager) extends ICommand {
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