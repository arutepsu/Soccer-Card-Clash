package util

import controller.command.ICommand

class UndoManager {
  private var undoStack: List[ICommand] = Nil
  private var redoStack: List[ICommand] = Nil

  def doStep(command: ICommand): Unit = {
    if (command.doStep()) {
      undoStack = command :: undoStack
      redoStack = Nil
    }
  }

  def undoStep(): Unit = {
    undoStack match {
      case Nil =>
      case head :: stack =>
        head.undoStep()
        undoStack = stack
        redoStack = head :: redoStack
    }
  }

  def redoStep(): Unit = {
    redoStack match {
      case Nil =>
      case head :: stack => {
        head.redoStep()
        redoStack = stack
        undoStack = head :: undoStack
      }
    }
  }
}
