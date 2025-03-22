package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import de.htwg.se.soccercardclash.controller.command.memento.factory.IMementoManagerFactory
import scala.util.{Failure, Success}

// Subclass to expose protected members
private class TestableReverseSwapActionCommand(game: IGame, factory: IMementoManagerFactory)
  extends ReverseSwapActionCommand(game, factory) {

  def setSwapSuccessful(success: Boolean): Unit = {
    this.swapSuccessful = Some(success)
  }

  def testExecuteAction(): Boolean = executeAction()
}

class ReverseSwapActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ReverseSwapActionCommand" should {

    "execute successfully when actionManager.reverseSwap returns true" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.reverseSwap()).thenReturn(true)

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).reverseSwap()
    }

    "execute unsuccessfully when actionManager.reverseSwap returns false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.reverseSwap()).thenReturn(false)

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).reverseSwap()
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.reverseSwap()).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableReverseSwapActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).reverseSwap()
    }
  }
}