package controller.command.base.workflow

import controller.command.ICommand
import model.gameComponent.IGame
import controller.{Events, IController}
import scala.util.{Try, Success, Failure}

abstract class WorkflowCommand extends ICommand {
  override def undoStep(): Unit = {}
  override def redoStep(): Unit = doStep()
}

class CreateGameWorkflowCommand(val game: IGame, player1: String, player2: String) extends WorkflowCommand {
  override def doStep(): Boolean = {
    Try {
      game.createGame(player1, player2)
    } match {
      case Success(_) => true
      case Failure(exception) =>
        false
    }
  }
}

class QuitWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Boolean = true
}

class SaveGameWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Boolean = {
    Try(game.saveGame()) match {
      case Success(_) => true
      case Failure(exception) =>
        false
    }
  }
}

class LoadGameWorkflowCommand(val game: IGame, val fileName: String) extends WorkflowCommand {
  override def doStep(): Boolean = {
    Try(game.loadGame(fileName)) match {
      case Success(_) => true
      case Failure(exception) =>
        false
    }
  }
}
