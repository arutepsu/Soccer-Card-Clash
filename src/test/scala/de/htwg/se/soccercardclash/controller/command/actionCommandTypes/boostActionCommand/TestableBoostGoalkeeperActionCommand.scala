package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommand

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostGoalkeeperActionCommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.mockito.ArgumentMatchers.{eq => meq}

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

  private class TestableBoostGoalkeeperActionCommand(
                                                      game: IGame,
                                                      factory: IMementoManagerFactory
                                                    ) extends BoostGoalkeeperActionCommand(game, factory) {

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

  "BoostGoalkeeperActionCommand" should {

    "execute successfully when actionManager.boostGoalkeeper returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostGoalkeeper(meq(mockActionService))).thenReturn(true)

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).boostGoalkeeper(meq(mockActionService))
    }

    "execute unsuccessfully when actionManager.boostGoalkeeper returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostGoalkeeper(meq(mockActionService))).thenReturn(false)

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper(meq(mockActionService))
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostGoalkeeper(meq(mockActionService)))
        .thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostGoalkeeper(meq(mockActionService))
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
      val mockActionService = mock[IPlayerActionManager]
      val mockMementoManager = mock[IMementoManager]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostGoalkeeper(meq(mockActionService))).thenReturn(true)

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
      verify(mockActionManager).boostGoalkeeper(meq(mockActionService))
    }

    "not redo goalkeeper boost if no memento exists" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]

      val command = new TestableBoostGoalkeeperActionCommand(mockGame, mockFactory)

      command.redoStep()

      succeed
    }
  }
}
