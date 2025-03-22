package controller.command.actionCommandTypes.gameStateCommands

import controller.command.ICommand
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import scala.util.{Failure, Success}
import controller.command.memento.factory.IMementoManagerFactory

// Subclass to expose private members
private class TestableResetGameCommand(game: IGame, factory: IMementoManagerFactory)
  extends ResetGameCommand(game, factory) {

  def setResetSuccessful(success: Boolean): Unit = {
    val field = classOf[ResetGameCommand].getDeclaredField("resetSuccessful")
    field.setAccessible(true)
    field.set(this, Some(success))
  }

  def testExecuteAction(): Boolean = executeAction()
}

class ResetGameCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ResetGameCommand" should {

    "execute successfully when game.reset() returns true" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.reset()).thenReturn(true)

      val command = new TestableResetGameCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockGame).reset()
    }

    "execute unsuccessfully when game.reset() returns false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.reset()).thenReturn(false)

      val command = new TestableResetGameCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockGame).reset()
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.reset()).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableResetGameCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockGame).reset()
    }
  }
}
