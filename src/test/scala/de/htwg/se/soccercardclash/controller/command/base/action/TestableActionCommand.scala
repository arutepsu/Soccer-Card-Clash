package de.htwg.se.soccercardclash.controller.command.base.action

import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*

private class TestableActionCommand(game: IGame, factory: IMementoManagerFactory)
  extends ActionCommand(game, factory) {

  private var actionResult: Boolean = true

  def setActionResult(result: Boolean): Unit = {
    this.actionResult = result
  }

  override protected def executeAction(): Boolean = actionResult

  def setMementoManager(mockMementoManager: IMementoManager): Unit = {
    val field = classOf[ActionCommand].getDeclaredField("mementoManager")
    field.setAccessible(true)
    field.set(this, mockMementoManager)
  }

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
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mockActionManager)
        when(mockMementoManager.createMemento()).thenReturn(mockMemento)

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)

        val result = command.doStep()

        result shouldBe true
        verify(mockMementoManager).createMemento()
        verify(mockGame).updateGameState()
      }

      "should not store a memento when doStep() fails" in {
        val mockGame = mock[IGame]
        val mockMementoManager = mock[IMementoManager]
        val mockMemento = mock[Memento]
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mock[IActionManager])
        when(mockMementoManager.createMemento()).thenReturn(mockMemento)

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)
        command.setActionResult(false)

        val result = command.doStep()

        result shouldBe false
        verify(mockMementoManager).createMemento()
        verify(mockGame, never()).updateGameState()
      }

      "restore game state when undoStep() is called" in {
        val mockGame = mock[IGame]
        val mockMementoManager = mock[IMementoManager]
        val mockMemento = mock[Memento]
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mock[IActionManager])

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)
        command.setMemento(Some(mockMemento))

        command.undoStep()

        verify(mockMementoManager).restoreGameState(mockMemento)
        verify(mockGame).updateGameState()
      }

      "do nothing when undoStep() is called without a memento" in {
        val mockGame = mock[IGame]
        val mockMementoManager = mock[IMementoManager]
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mock[IActionManager])

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)
        command.setMemento(None)

        command.undoStep()

        verify(mockMementoManager, never()).restoreGameState(any[Memento])
        verify(mockGame, never()).updateGameState()
      }

      "reapply command when redoStep() is called" in {
        val mockGame = mock[IGame]
        val mockMementoManager = mock[IMementoManager]
        val mockMemento = mock[Memento]
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mock[IActionManager])
        when(mockMementoManager.createMemento()).thenReturn(mockMemento)

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)
        command.setMemento(Some(mockMemento))

        command.redoStep()

        verify(mockMementoManager, never()).restoreGameState(any[Memento])
        verify(mockMementoManager).createMemento()
        verify(mockGame).updateGameState()
      }

      "do nothing when redoStep() is called without a memento" in {
        val mockGame = mock[IGame]
        val mockMementoManager = mock[IMementoManager]
        val mockFactory = mock[IMementoManagerFactory]

        when(mockGame.getActionManager).thenReturn(mock[IActionManager])

        val command = new TestableActionCommand(mockGame, mockFactory)
        command.setMementoManager(mockMementoManager)
        command.setMemento(None)

        command.redoStep()

        verify(mockMementoManager, never()).restoreGameState(any[Memento])
        verify(mockMementoManager, never()).createMemento()
        verify(mockGame, never()).updateGameState()
      }
    }
  }