package de.htwg.se.soccercardclash.util

import de.htwg.se.soccercardclash.controller.command.{CommandResult, ICommand}
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
class UndoManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "UndoManager" should {

    "store executed commands and allow undo" in {
      val command = mock[ICommand]
      val oldState = mock[IGameState]
      val newState = mock[IGameState]
      val memento = mock[Memento]

      when(command.execute(oldState)).thenReturn(CommandResult(success = true, newState, List.empty))
      when(oldState.createMemento()).thenReturn(memento)
      when(newState.createMemento()).thenReturn(mock[Memento])
      when(newState.restoreFromMemento(memento)).thenReturn(oldState)

      val undoManager = new UndoManager
      val result = undoManager.doStep(command, oldState)

      result.state shouldBe newState

      val (undoState, undoEvents) = undoManager.undoStep(newState)
      undoState shouldBe oldState
      undoEvents should contain only GameActionEvent.Undo
    }

    "not push to undo stack if execution fails" in {
      val command = mock[ICommand]
      val oldState = mock[IGameState]
      val failedResult = CommandResult(success = false, oldState, List.empty)

      when(command.execute(oldState)).thenReturn(failedResult)

      val undoManager = new UndoManager
      undoManager.doStep(command, oldState)

      val (undoState, undoEvents) = undoManager.undoStep(oldState)
      undoState shouldBe oldState
      undoEvents shouldBe empty
    }

    "support redo after undo" in {
      val command = mock[ICommand]
      val oldState = mock[IGameState]
      val newState = mock[IGameState]
      val memento = mock[Memento]

      when(command.execute(oldState)).thenReturn(CommandResult(success = true, newState, List.empty))
      when(oldState.createMemento()).thenReturn(memento)
      when(newState.createMemento()).thenReturn(memento)
      when(newState.restoreFromMemento(memento)).thenReturn(oldState)
      when(oldState.restoreFromMemento(memento)).thenReturn(newState)

      val undoManager = new UndoManager
      undoManager.doStep(command, oldState)

      undoManager.undoStep(newState)

      val (redoState, redoEvents) = undoManager.redoStep(oldState)
      redoState shouldBe newState
      redoEvents should contain only GameActionEvent.Redo
    }


    "do nothing on undo when stack is empty" in {
      val undoManager = new UndoManager
      val state = mock[IGameState]

      val (undoState, events) = undoManager.undoStep(state)
      undoState shouldBe state
      events shouldBe empty
    }

    "do nothing on redo when stack is empty" in {
      val undoManager = new UndoManager
      val state = mock[IGameState]

      val (redoState, events) = undoManager.redoStep(state)
      redoState shouldBe state
      events shouldBe empty
    }
  }
}
