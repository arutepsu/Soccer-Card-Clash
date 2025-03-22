package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.attackActionCommands.SingleAttackActionCommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.IActionManager
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.controller.command.memento.factory.IMementoManagerFactory

import scala.util.{Failure, Success}

class SingleAttackActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Subclass to expose `executeAction` for testing
  private class TestableSingleAttackActionCommand(defenderIndex: Int, game: IGame, factory: IMementoManagerFactory)
    extends SingleAttackActionCommand(defenderIndex, game, factory) {
    def testExecuteAction(): Boolean = executeAction()
  }

  "SingleAttackActionCommand" should {

    "execute successfully when actionManager.singleAttack returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.singleAttack(anyInt())).thenReturn(true)

      val command = new TestableSingleAttackActionCommand(defenderIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).singleAttack(1)
    }

    "execute unsuccessfully when actionManager.singleAttack returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.singleAttack(anyInt())).thenReturn(false)

      val command = new TestableSingleAttackActionCommand(defenderIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).singleAttack(1)
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.singleAttack(anyInt())).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableSingleAttackActionCommand(defenderIndex = 1, game = mockGame, factory = mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).singleAttack(1)
    }
  }
}