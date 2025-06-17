package de.htwg.se.soccercardclash.controller.command.workflow

import de.htwg.se.soccercardclash.controller.IController
import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.service.IGameService
import de.htwg.se.soccercardclash.util.*

import scala.util.{Failure, Success, Try}


abstract class WorkflowCommand extends ICommand {

  def doStep(state: IGameState): (IGameState, List[ObservableEvent])

  override def execute(state: IGameState): CommandResult = {
    val (newState, events) = doStep(state)
    CommandResult(success = true, newState, events)
  }
}

class CreateGameWorkflowCommand(
                                 gameService: IGameService,
                                 player1: String,
                                 player2: String
                               ) extends WorkflowCommand {

  override def doStep(state: IGameState): (IGameState, List[ObservableEvent]) = {
    val newState = gameService.createNewGame(player1, player2)
    (newState, List(SceneSwitchEvent.PlayingField))
  }
}

class QuitWorkflowCommand(val exitStrategy: ExitStrategy) extends WorkflowCommand {
  override def doStep(state: IGameState): (IGameState, List[ObservableEvent]) = {
    exitStrategy.exit()
    (state, List.empty)
  }
}


class SaveGameWorkflowCommand(
                               gameService: IGameService
                             ) extends WorkflowCommand {

  override def doStep(state: IGameState): (IGameState, List[ObservableEvent]) = {
    val success = gameService.saveGame(state).isSuccess
    val event = GameActionEvent.SaveGame
    (state, List(event))
  }
}

class LoadGameWorkflowCommand(
                               gameService: IGameService,
                               fileName: String
                             ) extends WorkflowCommand {

  override def doStep(state: IGameState): (IGameState, List[ObservableEvent]) = {
    gameService.loadGame(fileName) match {
      case Success(loadedState) =>
        (loadedState, List(SceneSwitchEvent.LoadGame))
      case Failure(_) =>
        (state, List.empty)
    }
  }
}

trait ExitStrategy {
  def exit(): Unit
}
object ExitGameStrategy extends ExitStrategy {
  override def exit(): Unit = System.exit(0)
}
