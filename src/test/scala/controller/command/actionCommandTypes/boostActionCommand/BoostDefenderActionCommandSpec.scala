package controller.command.actionCommandTypes.boostActionCommand

import controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import controller.command.base.action.ActionCommand
import model.gameComponent.IGame
import model.playingFiledComponent.manager.IActionManager
import controller.command.memento.base.*
import controller.command.memento.IMementoManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import model.playerComponent.IPlayer
import model.cardComponent.ICard

import scala.util.{Failure, Success}

private class TestableBoostDefenderActionCommand(cardIndex: Int, game: IGame)
  extends BoostDefenderActionCommand(cardIndex, game) {

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

class BoostDefenderActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostDefenderActionCommand" should {

    "execute successfully when actionManager.boostDefender returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostDefender(anyInt())).thenReturn(true)

      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).boostDefender(1)
    }

    "execute unsuccessfully when actionManager.boostDefender returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostDefender(anyInt())).thenReturn(false)

      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostDefender(1)
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.boostDefender(anyInt())).thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostDefender(1)
    }

    "undo boost when a memento exists" in {
      val mockGame = mock[IGame]
      val mockMementoManager = mock[IMementoManager]

      // Creating real Memento instance with mocked players and cards
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

      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))

      command.undoStep()

      verify(mockMementoManager).restoreBoosts(memento, 1)
    }

    "redo boost when a memento exists and boost was successful" in {
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

      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))
      command.setBoostSuccessful(true)

      command.redoStep()

      verify(mockMementoManager).restoreGameState(memento)
      verify(mockActionManager).boostDefender(1)
    }

    "not redo boost if no memento exists" in {
      val mockGame = mock[IGame]
      val command = new TestableBoostDefenderActionCommand(cardIndex = 1, game = mockGame)

      command.redoStep() // No memento set

      // No interaction with mocks should happen
      succeed
    }
  }
}