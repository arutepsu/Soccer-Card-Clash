package de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommand

import de.htwg.se.soccercardclash.controller.command.actionCommandTypes.boostActionCommands.BoostDefenderActionCommand
import de.htwg.se.soccercardclash.controller.command.base.action.ActionCommand
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.{IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import org.mockito.ArgumentMatchers.{anyInt, eq as meq}

import scala.util.{Failure, Success}

private class TestableBoostDefenderActionCommand(cardIndex: Int, game: IGame, factory: IMementoManagerFactory)
  extends BoostDefenderActionCommand(cardIndex, game, factory) {

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

class BoostDefenderAIActionCommandSpec extends AnyWordSpec with Matchers with MockitoSugar {

  private class TestableBoostDefenderActionCommand(
                                                    cardIndex: Int,
                                                    game: IGame,
                                                    factory: IMementoManagerFactory
                                                  ) extends BoostDefenderActionCommand(cardIndex, game, factory) {

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

  "BoostDefenderActionCommand" should {

    "execute successfully when actionManager.boostDefender returns true" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostDefender(meq(1), meq(mockActionService))).thenReturn(true)

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe true
      verify(mockActionManager).boostDefender(meq(1), meq(mockActionService))
    }

    "execute unsuccessfully when actionManager.boostDefender returns false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostDefender(meq(1), meq(mockActionService))).thenReturn(false)

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostDefender(meq(1), meq(mockActionService))
    }

    "handle exceptions and return false" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostDefender(meq(1), meq(mockActionService)))
        .thenThrow(new RuntimeException("Test Exception"))

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      val result = command.testExecuteAction()

      result shouldBe false
      verify(mockActionManager).boostDefender(meq(1), meq(mockActionService))
    }

    "undo boost when a memento exists" in {
      val mockGame = mock[IGame]
      val mockFactory = mock[IMementoManagerFactory]
      val mockMementoManager = mock[IMementoManager]

      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]

      // ✅ Mock getActionManager and getPlayerActionService
      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)

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

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))

      command.undoStep()

      verify(mockMementoManager).restoreBoosts(memento, 1)
      verifyNoMoreInteractions(mockMementoManager)
    }


    "redo boost when a memento exists and boost was successful" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]
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
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)
      when(mockActionManager.boostDefender(meq(1), meq(mockActionService))).thenReturn(true)

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      command.setMementoManager(mockMementoManager)
      command.setMemento(Some(memento))
      command.setBoostSuccessful(true)

      command.redoStep()

      verify(mockMementoManager).restoreGameState(memento)
      verify(mockActionManager).boostDefender(meq(1), meq(mockActionService))
    }

    "not redo boost if no memento exists" in {
      val mockGame = mock[IGame]
      val mockActionManager = mock[IActionManager]
      val mockActionService = mock[IPlayerActionManager]
      val mockFactory = mock[IMementoManagerFactory]

      when(mockGame.getActionManager).thenReturn(mockActionManager)
      when(mockActionManager.getPlayerActionService).thenReturn(mockActionService)

      val command = new TestableBoostDefenderActionCommand(1, mockGame, mockFactory)
      command.setBoostSuccessful(true)

      command.redoStep()

      // ✅ Verify boostDefender is *not* called
      verify(mockActionManager, never()).boostDefender(anyInt(), any[IPlayerActionManager])
    }
  }
}