package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState

class UndoManager {
  private var undoStack: List[(ICommand, IGameState)] = Nil
  private var redoStack: List[(ICommand, IGameState)] = Nil

  def doStep(command: ICommand, currentState: IGameState): CommandResult = {
    val result = command.execute(currentState)

    if (result.success) {
      undoStack = (command, currentState) :: undoStack
      redoStack = Nil
      println(s"[UndoManager] ✅ Executed ${command.getClass.getSimpleName}, pushed to undo stack.")
    } else {
      println(s"[UndoManager] ❌ Command ${command.getClass.getSimpleName} failed, no state change.")
    }

    result
  }

  def undoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    undoStack match {
      case (command, prevState) :: rest =>
        undoStack = rest
        redoStack = (command, currentState) :: redoStack
        println("[UndoManager] 🔄 Undo → restoring previous state.")
        (prevState, List(Events.Undo))

      case Nil =>
        println("[UndoManager] ⚠️ Nothing to undo.")
        (currentState, List.empty)
    }
  }

  def redoStep(currentState: IGameState): (IGameState, List[ObservableEvent]) = {
    redoStack match {
      case (command, nextState) :: rest =>
        redoStack = rest
        undoStack = (command, currentState) :: undoStack
        println("[UndoManager] 🔁 Redo → restoring next state.")
        (nextState, List(Events.Redo))

      case Nil =>
        println("[UndoManager] ⚠️ Nothing to redo.")
        (currentState, List.empty)
    }
  }

  def clear(): Unit = {
    undoStack = Nil
    redoStack = Nil
    println("[UndoManager] 🧹 Cleared undo/redo stacks.")
  }
}
