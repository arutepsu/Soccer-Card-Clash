package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState

class UndoManager {
  private var undoStack: List[(ICommand, Memento)] = Nil
  private var redoStack: List[(ICommand, Memento)] = Nil

  def doStep(command: ICommand, currentState: IGameState): CommandResult = {
    val result = command.execute(currentState)

    if (result.success) {
      undoStack = (command, currentState.createMemento()) :: undoStack
      redoStack = Nil
    }

    result
  }

  def undoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    undoStack match {
      case (command, memento) :: rest =>
        undoStack = rest
        redoStack = (command, currentState.createMemento()) :: redoStack
        (currentState.restoreFromMemento(memento), List(GameActionEvent.Undo))

      case Nil =>
        (currentState, List.empty)
    }
  }

  def redoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    redoStack match {
      case (command, memento) :: rest =>
        redoStack = rest
        undoStack = (command, currentState.createMemento()) :: undoStack
        (currentState.restoreFromMemento(memento), List(GameActionEvent.Redo))

      case Nil =>
        (currentState, List.empty)
    }
  }
}
