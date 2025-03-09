package controller.command.base.workflow

import controller.command.ICommand
import model.gameComponent.IGame


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
      println("✅ SaveGameWorkflowCommand: Game saved successfully.")
    } catch {
      case e: Exception =>
        println(s"❌ SaveGameWorkflowCommand: Error saving game - ${e.getMessage}")
    }
  }
}

class LoadGameWorkflowCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Unit = {
    try {
      game.loadGame()
      println("✅ LoadGameWorkflowCommand: Game loaded successfully.")
    } catch {
      case e: Exception =>
        println(s"❌ LoadGameWorkflowCommand: Error loading game - ${e.getMessage}")
    }
  }
}

