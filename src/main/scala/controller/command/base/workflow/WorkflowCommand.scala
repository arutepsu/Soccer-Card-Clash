package controller.command.base.workflow

import controller.command.ICommand
import model.gameComponent.IGame
import controller.{Events, IController}

abstract class WorkflowCommand extends ICommand {
  override def undoStep(): Unit = {}
  override def redoStep(): Unit = doStep()
}

class StartGameWorkflowCommand(val game: IGame, player1: String, player2: String) extends WorkflowCommand {
  override def doStep(): Unit = game.startGame(player1, player2)
}

class QuitWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Unit = game.exit()
}

class SaveGameWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Unit = {
    try {
      game.saveGame()
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error while saving the game", e)
    }
  }
}

class LoadGameWorkflowCommand(val game: IGame, val fileName: String) extends WorkflowCommand {

  override def doStep(): Unit = {
    try {
      game.loadGame(fileName)
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error while loading the game", e)
    }
  }
}



