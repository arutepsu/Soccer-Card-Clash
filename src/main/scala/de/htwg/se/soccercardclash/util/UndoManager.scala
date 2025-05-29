package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState

class UndoManager {
  private var undoStack: List[(ICommand, IGameState)] = Nil
  private var redoStack: List[(ICommand, IGameState)] = Nil

  def doStep(command: ICommand, currentState: IGameState): CommandResult = {
    val result = command.execute(currentState)

    if (result.success) {
      undoStack = (command, currentState) :: undoStack
      redoStack = Nil
    }

    result
  }

  def undoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    undoStack match {
      case (command, prevState) :: rest =>
        undoStack = rest
        redoStack = (command, currentState) :: redoStack
        (prevState, List(GameActionEvent.Undo))

      case Nil =>
        (currentState, List.empty)
    }
  }

  def redoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    redoStack match {
      case (command, nextState) :: rest =>
        redoStack = rest
        undoStack = (command, currentState) :: undoStack
        (nextState, List(GameActionEvent.Redo))

      case Nil =>
        (currentState, List.empty)
    }
  }

}
