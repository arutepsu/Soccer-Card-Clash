package de.htwg.se.soccercardclash.controller.command.base.workflow

import de.htwg.se.soccercardclash.controller.command.ICommand
import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.util.Events

import scala.util.{Failure, Success, Try}


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
  override def doStep(): Boolean = {
    Try(game.exit()) match {
      case Success(_) => true
      case Failure(exception) =>
        false
    }
  }
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
