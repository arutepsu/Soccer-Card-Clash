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
      val result = CommandResult(success = true, state = newState, events = List.empty)


      when(command.execute(oldState)).thenReturn(result)

      val undoManager = new UndoManager
      val commandResult = undoManager.doStep(command, oldState)

      commandResult shouldBe result

      val (undoState, undoEvents) = undoManager.undoStep(newState)
      undoState shouldBe oldState
      undoEvents should contain only GameActionEvent.Undo
    }

    "not push to undo stack if execution fails" in {
      val command = mock[ICommand]
      val oldState = mock[IGameState]
      val result = CommandResult(success = false, state = oldState, events = List.empty)


      when(command.execute(oldState)).thenReturn(result)

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
      val result = CommandResult(success = true, state = newState, events = List.empty)


      when(command.execute(oldState)).thenReturn(result)

      val undoManager = new UndoManager
      undoManager.doStep(command, oldState)
      val (_, _) = undoManager.undoStep(newState)

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
