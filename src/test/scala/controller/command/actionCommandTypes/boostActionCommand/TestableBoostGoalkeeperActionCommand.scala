package controller.command.actionCommandTypes.boostActionCommand

import controller.command.actionCommandTypes.boostActionCommands.BoostGoalkeeperActionCommand
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import controller.command.memento.base.*
import controller.command.memento.IMementoManager
import model.playerComponent.IPlayer
import model.cardComponent.ICard
import model.playerComponent.playerAction.PlayerActionState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import controller.command.memento.factory.IMementoManagerFactory

import scala.util.{Failure, Success}

// Updated subclass to pass factory
private class TestableBoostGoalkeeperActionCommand(game: IGame, factory: IMementoManagerFactory)
  extends BoostGoalkeeperActionCommand(game, factory) {

  def setMementoManager(mockMementoManager: IMementoManager): Unit = {
    this.mementoManager = mockMementoManager
  }

  def setMemento(mockMemento: Option[Memento]): Unit = {
    this.memento = mockMemento
  }

  def setBoostSuccessful(success: Boolean): Unit = {
    this.boostSuccessful = Some(success)
  }

  def testExecuteAction(): Boolean = executeAction()
}

class BoostGoalkeeperActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostGoalkeeperActionCommand" should {

    "execute successfully when actionManager.boostGoalkeeper returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenReturn(true)

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).boostGoalkeeper()
    }

    "execute unsuccessfully when actionManager.boostGoalkeeper returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenReturn(false)

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper()
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper()
    }

    "undo goalkeeper boost when a memento exists" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockMementoManager = mock[IMementoManager]

      val memento = Memento(
        attacker = mock[IPlayer],
        defender = mock[IPlayer],
        player1Defenders = List(mock[ICard]),
        player2Defenders = List(mock[ICard]),
        player1Goalkeeper = None,
        player2Goalkeeper = None,
        player1Hand = List(),
        player2Hand = List(),
        player1Score = 0,
        player2Score = 0,
        player1Actions = Map.empty,
        player2Actions = Map.empty
      )

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))

      command.undoStep()

      verify(mockMementoManager).restoreGoalkeeperBoost(memento)
    }

    "redo goalkeeper boost when a memento exists and boost was successful" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockActionManager = mock[IActionManager]
      val mockMementoManager = mock[IMementoManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)

      val memento = Memento(
        attacker = mock[IPlayer],
        defender = mock[IPlayer],
        player1Defenders = List(mock[ICard]),
        player2Defenders = List(mock[ICard]),
        player1Goalkeeper = None,
        player2Goalkeeper = None,
        player1Hand = List(),
        player2Hand = List(),
        player1Score = 0,
        player2Score = 0,
        player1Actions = Map.empty,
        player2Actions = Map.empty
      )

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))
      command.setBoostSuccessful(true)

      command.redoStep()

      verify(mockMementoManager).restoreGameState(memento)
      verify(mockActionManager).boostGoalkeeper()
    }

    "not redo goalkeeper boost if no memento exists" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)

      command.redoStep()

      // Success if no exception thrown
      succeed
    }
  }
}