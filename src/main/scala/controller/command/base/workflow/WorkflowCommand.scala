package controller.command.base.workflow

import controller.command.ICommand
import model.gameComponent.IGame
import controller.{Events, IController}

abstract class WorkflowCommand extends ICommand {
  override def undoStep(): Unit = {}
  override def redoStep(): Unit = doStep()
}

class StartGameWorkflowCommand(val game: IGame, player1: String, player2: String) extends WorkflowCommand {

  override def doStep(): Boolean = {
    if (game.getPlayingField != null) {
      game.getPlayingField.reset()
    }
    game.startGame(player1, player2)
    true
  }
}


class QuitWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Boolean = {
    game.exit()
    true
  }
}

class SaveGameWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Boolean = {
    try {
      game.saveGame()
      true
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error while saving the game", e)
        false
    }
  }
}

class LoadGameWorkflowCommand(val game: IGame, val fileName: String) extends WorkflowCommand {

  override def doStep(): Boolean = {
    try {
      game.loadGame(fileName)
      true
    } catch {
      case e: Exception =>
        throw new RuntimeException("Error while loading the game", e)
        false
    }
  }
}



