package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.DoubleAttackActionCommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory

import org.mockito.ArgumentMatchers.{anyInt, eq => meq}

class DoubleAttackActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  private class TestableDoubleAttackActionCommand(
                                                   defenderIndex: Int,
                                                   game: IGame,
                                                   factory: IMementoManagerFactory
                                                 ) extends DoubleAttackActionCommand(defenderIndex, game, factory) {
    def testExecuteAction(): Boolean = executeAction()
  }

  "DoubleAttackActionCommand" should {

    "execute successfully when actionManager.doubleAttack returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.doubleAttack(meq(1), meq(mockActionService))).thenReturn(true)

      val command = new TestableDoubleAttackActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).doubleAttack(meq(1), meq(mockActionService))
    }

    "execute unsuccessfully when actionManager.doubleAttack returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.doubleAttack(meq(1), meq(mockActionService))).thenReturn(false)

      val command = new TestableDoubleAttackActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).doubleAttack(meq(1), meq(mockActionService))
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.doubleAttack(meq(1), meq(mockActionService)))
        .thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableDoubleAttackActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).doubleAttack(meq(1), meq(mockActionService))
    }
  }
}