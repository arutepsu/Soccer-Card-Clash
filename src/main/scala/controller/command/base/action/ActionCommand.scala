package controller.command.base.action

import controller.command.ICommand
import controller.command.memento.*
import controller.command.memento.base.{Memento, MementoManager}
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager

abstract class ActionCommand(val game: IGame) extends ICommand {
  protected val actionManager: IActionManager = game.getActionManager
//  protected val mementoManager: IMementoManager = new MementoManager(actionManager)
  protected val mementoManager: IMementoManager = new MementoManager(game)

  protected var memento: Option[Memento] = None

  override def doStep(): Unit = {
    memento = Some(mementoManager.createMemento()) // ✅ Save current state before executing
    executeAction()
    game.updateGameState() // ✅ Ensure new game state is stored
  }

  override def undoStep(): Unit = {
    memento.foreach { savedMemento =>
      mementoManager.restoreGameState(savedMemento) // ✅ Restore previous game state
      game.updateGameState() // ✅ Ensure game updates correctly
    }
  }

  override def redoStep(): Unit = {
    doStep()
  }

  protected def executeAction(): Unit

}