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

class SaveGameWorkflowCommand() extends WorkflowCommand {
  override def doStep(): Unit = println("Game saved")
}

class LoadGameWorkflowCommand() extends WorkflowCommand {
  override def doStep(): Unit = println("Game loaded")
}
