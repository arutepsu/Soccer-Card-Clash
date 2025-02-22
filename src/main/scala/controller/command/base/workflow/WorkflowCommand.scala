package controller.command.base.workflow

import controller.command.ICommand
import model.gameComponent.IGame


abstract class WorkflowCommand extends ICommand {
  override def undoStep(): Unit = {}
  override def redoStep(): Unit = doStep()
}

class StartGameCommand(val game: IGame, player1: String, player2: String) extends WorkflowCommand {
  override def doStep(): Unit = game.startGame(player1, player2)
}

class QuitCommand(val game: IGame) extends WorkflowCommand {
  override def doStep(): Unit = game.exit()
}

class SaveGameCommand() extends WorkflowCommand {
  override def doStep(): Unit = println("Game saved")
}

class LoadGameCommand() extends WorkflowCommand {
  override def doStep(): Unit = println("Game loaded")
}
