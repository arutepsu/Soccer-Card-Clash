package controller.command.base.action

import controller.command.memento.IMementoManager
import controller.command.memento.base.{Memento, MementoManager}
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

// Concrete subclass for testing
private class TestableActionCommand(game: IGame) extends ActionCommand(game) {
  private var actionResult: Boolean = true

  def setActionResult(result: Boolean): Unit = {
    this.actionResult = result
  }

  override protected def executeAction(): Boolean = actionResult

  // Use reflection to set protected `mementoManager`
  def setMementoManager(mockMementoManager: IMementoManager): Unit = {
    val field = classOf[ActionCommand].getDeclaredField("mementoManager")
    field.setAccessible(true)
    field.set(this, mockMementoManager)
  }

  // Use reflection to set protected `memento`
  def setMemento(mockMemento: Option[Memento]): Unit = {
    val field = classOf[ActionCommand].getDeclaredField("memento")
    field.setAccessible(true)
    field.set(this, mockMemento)
  }
}

class ActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ActionCommand" should {

    "execute successfully and store a memento when doStep() is called" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockMementoManager = mock[IMementoManager]
      val mockMemento = mock[Memento]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockMementoManager.createMemento()).thenReturn(mockMemento)

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager) // Using reflection to override mementoManager

      val result = command.doStep()

      result shouldBe true
      verify(mockMementoManager).createMemento()
      verify(mockGame).updateGameState()
    }

    "should not store a memento when doStep() fails" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]
      val mockMemento = mock[Memento]

      when(mockGame.getActionManager).thenReturn(mock[IActionManager])
      when(mockMementoManager.createMemento()).thenReturn(mockMemento)

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager)
      command.setActionResult(false) // Simulating failure

      val result = command.doStep()

      result shouldBe false
      verify(mockMementoManager, times(1)).createMemento() // Memento is still created
      verify(mockGame, never()).updateGameState() // But game state is NOT updated
    }

    "restore game state when undoStep() is called" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]
      val mockMemento = mock[Memento]

      when(mockGame.getActionManager).thenReturn(mock[IActionManager])

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(mockMemento)) // Using reflection to override memento

      command.undoStep()

      verify(mockMementoManager).restoreGameState(mockMemento)
      verify(mockGame).updateGameState()
    }

    "do nothing when undoStep() is called without a memento" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]

      when(mockGame.getActionManager).thenReturn(mock[IActionManager])

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(None) // Using reflection to override memento

      command.undoStep()

      verify(mockMementoManager, never()).restoreGameState(any[Memento])
      verify(mockGame, never()).updateGameState()
    }

    "reapply command when redoStep() is called" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]
      val mockMemento = mock[Memento]

      when(mockGame.getActionManager).thenReturn(mock[IActionManager])
      when(mockMementoManager.createMemento()).thenReturn(mockMemento)

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(mockMemento)) // Using reflection to override memento

      command.redoStep()

      verify(mockMementoManager, never()).restoreGameState(any[Memento]) // Redo shouldn't restore
      verify(mockMementoManager).createMemento() // Should reapply
      verify(mockGame).updateGameState()
    }

    "do nothing when redoStep() is called without a memento" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]

      when(mockGame.getActionManager).thenReturn(mock[IActionManager])

      val command = new TestableActionCommand(mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(None) // Using reflection to override memento

      command.redoStep()

      verify(mockMementoManager, never()).restoreGameState(any[Memento])
      verify(mockMementoManager, never()).createMemento()
      verify(mockGame, never()).updateGameState()
    }
  }
}