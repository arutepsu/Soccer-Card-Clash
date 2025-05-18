package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import org.mockito.ArgumentMatchers.eq as meq
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager, IPlayerActionManager}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import scala.util.{Failure, Success}

// Subclass to expose protected members
private class TestableReverseSwapActionCommand(game: IGame, factory: IMementoManagerFactory)
  extends ReverseSwapActionCommand(game, factory) {

  def setSwapSuccessful(success: Boolean): Unit = {
    this.swapSuccessful = Some(success)
  }

  def testExecuteAction(): Boolean = executeAction()
}

class ReverseSwapAIActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ReverseSwapActionCommand" should {

    "execute successfully when actionManager.reverseSwap returns true" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.reverseSwap(meq(mockActionService))).thenReturn(true)

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).reverseSwap(meq(mockActionService))
    }

    "execute unsuccessfully when actionManager.reverseSwap returns false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.reverseSwap(meq(mockActionService))).thenReturn(false)

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).reverseSwap(meq(mockActionService))
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.reverseSwap(meq(mockActionService)))
        .thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).reverseSwap(meq(mockActionService))
    }
  }
}