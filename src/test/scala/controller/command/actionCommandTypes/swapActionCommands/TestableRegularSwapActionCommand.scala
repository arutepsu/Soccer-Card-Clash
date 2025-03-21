package controller.command.actionCommandTypes.swapActionCommands

import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import scala.util.{Failure, Success}
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import controller.command.memento.factory.IMementoManagerFactory

// Subclass to expose protected members
private class TestableRegularSwapActionCommand(cardIndex: Int, game: IGame, factory: IMementoManagerFactory)
  extends RegularSwapActionCommand(cardIndex, game, factory) {

  def setSwapSuccessful(success: Boolean): Unit = {
    this.swapSuccessful = Some(success)
  }

  def testExecuteAction(): Boolean = executeAction()
}

class RegularSwapActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RegularSwapActionCommand" should {

    "execute successfully when actionManager.regularSwap returns true" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.regularSwap(anyInt())).thenReturn(true)

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).regularSwap(1)
    }

    "execute unsuccessfully when actionManager.regularSwap returns false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.regularSwap(anyInt())).thenReturn(false)

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).regularSwap(1)
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.regularSwap(anyInt())).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).regularSwap(1)
    }
  }
}