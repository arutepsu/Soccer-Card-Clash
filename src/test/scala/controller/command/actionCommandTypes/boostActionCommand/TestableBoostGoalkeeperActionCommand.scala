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

import scala.util.{Failure, Success}

// Subclass to expose protected members
private class TestableBoostGoalkeeperActionCommand(game: IGame)
  extends BoostGoalkeeperActionCommand(game) {

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

  def setBoostSuccessful(success: Boolean): Unit = {
    this.boostSuccessful = Some(success) // Directly setting protected field
  }

  def testExecuteAction(): Boolean = executeAction()
}

class BoostGoalkeeperActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostGoalkeeperActionCommand" should {

    "execute successfully when actionManager.boostGoalkeeper returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenReturn(true)

      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).boostGoalkeeper()
    }

    "execute unsuccessfully when actionManager.boostGoalkeeper returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenReturn(false)

      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper()
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostGoalkeeper()).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper()
    }

    "undo goalkeeper boost when a memento exists" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]

      val mockAttacker = mock[IPlayer]
      val mockDefender = mock[IPlayer]
      val mockCard = mock[ICard]

      val memento = Memento(
        attacker = mockAttacker,
        defender = mockDefender,
        player1Defenders = List(mockCard),
        player2Defenders = List(mockCard),
        player1Goalkeeper = None,
        player2Goalkeeper = None,
        player1Hand = List(),
        player2Hand = List(),
        player1Score = 0,
        player2Score = 0,
        player1Actions = Map.empty,
        player2Actions = Map.empty
      )

      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))

      command.undoStep()

      verify(mockMementoManager).restoreGoalkeeperBoost(memento)
    }

    "redo goalkeeper boost when a memento exists and boost was successful" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockMementoManager = mock[IMementoManager]

      val mockAttacker = mock[IPlayer]
      val mockDefender = mock[IPlayer]
      val mockCard = mock[ICard]

      val memento = Memento(
        attacker = mockAttacker,
        defender = mockDefender,
        player1Defenders = List(mockCard),
        player2Defenders = List(mockCard),
        player1Goalkeeper = None,
        player2Goalkeeper = None,
        player1Hand = List(),
        player2Hand = List(),
        player1Score = 0,
        player2Score = 0,
        player1Actions = Map.empty,
        player2Actions = Map.empty
      )

      when(mockGame.getActionManager).thenReturn(mockActionManager)

      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))
      command.setBoostSuccessful(true) // Directly setting boostSuccessful

      command.redoStep()

      verify(mockMementoManager).restoreGameState(memento)
      verify(mockActionManager).boostGoalkeeper()
    }

    "not redo goalkeeper boost if no memento exists" in {
      val mockGame = mock[IGame]
      val command = new TestableBoostGoalkeeperActionCommand(game = mockGame)

      command.redoStep() // No memento set

      // No interaction with mocks should happen
      succeed
    }
  }
}