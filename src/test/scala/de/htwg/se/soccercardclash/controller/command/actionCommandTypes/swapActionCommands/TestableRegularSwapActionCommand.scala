package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.swapActionCommands

import org.mockito.ArgumentMatchers.{eq => meq}
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import scala.util.{Failure, Success}
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*

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
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.regularSwap(meq(1), meq(mockActionService))).thenReturn(true)

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).regularSwap(meq(1), meq(mockActionService))
    }

    "execute unsuccessfully when actionManager.regularSwap returns false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.regularSwap(meq(1), meq(mockActionService))).thenReturn(false)

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).regularSwap(meq(1), meq(mockActionService))
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.regularSwap(meq(1), meq(mockActionService)))
        .thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableRegularSwapActionCommand(cardIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).regularSwap(meq(1), meq(mockActionService))
    }
  }
}