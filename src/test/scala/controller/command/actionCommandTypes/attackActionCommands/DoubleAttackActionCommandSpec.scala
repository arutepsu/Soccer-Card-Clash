package controller.command.actionCommandTypes.attackActionCommands

import controller.command.actionCommandTypes.attackActionCommands.DoubleAttackActionCommand
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class DoubleAttackActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Subclass to expose `executeAction` for testing
  private class TestableDoubleAttackActionCommand(defenderIndex: Int, game: IGame)
    extends DoubleAttackActionCommand(defenderIndex, game) {
    def testExecuteAction(): Boolean = executeAction()
  }

  "DoubleAttackActionCommand" should {

    "execute successfully when actionManager.doubleAttack returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.doubleAttack(anyInt())).thenReturn(true)

      val command = new TestableDoubleAttackActionCommand(defenderIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).doubleAttack(1)
    }

    "execute unsuccessfully when actionManager.doubleAttack returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.doubleAttack(anyInt())).thenReturn(false)

      val command = new TestableDoubleAttackActionCommand(defenderIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).doubleAttack(1)
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.doubleAttack(anyInt())).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableDoubleAttackActionCommand(defenderIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).doubleAttack(1)
    }
  }
}
