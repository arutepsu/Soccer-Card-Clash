package de.htwg.se.soccercardclash.model.gameComponent.base

import de.htwg.se.soccercardclash.model.gameComponent.base.Game
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.service.{IGameInitializer, IGamePersistence}
import de.htwg.se.soccercardclash.model.gameComponent.state.{IGameState, IGameStateManager}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.manager.IActionManager
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scala.util.{Success, Failure}

class GameSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Game" should {
    val mockInitializer = mock[IGameInitializer]
    val mockStateManager = mock[IGameStateManager]
    val mockPersistence = mock[IGamePersistence]
    val mockField = mock[IGameState]
    val mockPlayer1 = mock[IPlayer]
    val mockPlayer2 = mock[IPlayer]
    val mockActionManager = mock[IActionManager]
    val mockState = mock[IGameState]

    val game: IGame = new Game(mockInitializer, mockStateManager, mockPersistence)

    when(mockInitializer.getPlayingField).thenReturn(mockField)
    when(mockInitializer.getPlayer1).thenReturn(mockPlayer1)
    when(mockInitializer.getPlayer2).thenReturn(mockPlayer2)
    when(mockInitializer.getActionManager).thenReturn(mockActionManager)
    when(mockStateManager.getGameState).thenReturn(mockState)

    "return correct references from initializer" in {
      game.getPlayingField shouldBe mockField
      game.getPlayer1 shouldBe mockPlayer1
      game.getPlayer2 shouldBe mockPlayer2
      game.getActionManager shouldBe mockActionManager
    }

    "create game and update game state" in {
      game.createGame("Alice", "Bob")
      verify(mockInitializer).createGame("Alice", "Bob")
      verify(mockStateManager).updateGameState(mockInitializer)
    }

    "update game state" in {
      reset(mockStateManager) // 👈 Reset to clear previous interactions

      game.updateGameState()

      verify(mockStateManager, times(1)).updateGameState(mockInitializer)
    }


    "save game via persistence layer" in {
      reset(mockStateManager, mockPersistence)
      when(mockStateManager.getGameState).thenReturn(mockState)

      game.saveGame()
      verify(mockPersistence).saveGame(mockState)
    }


    "load game and reinitialize initializer" in {
      when(mockPersistence.loadGame("game.json")).thenReturn(Success(mockState))

      game.loadGame("game.json")

      verify(mockInitializer).initializeFromState(mockState)
      verify(mockStateManager).updateGameState(mockInitializer)
    }

    "return Failure when loading game with no valid state" in {
      val exceptionMessage = "No valid game state found"
      when(mockPersistence.loadGame("badfile"))
        .thenReturn(Failure(new RuntimeException(exceptionMessage)))

      val result = game.loadGame("badfile")

      result.isFailure shouldBe true
      result.failed.get.getMessage should include(exceptionMessage)
    }


    "reset game" in {
      when(mockInitializer.getPlayingField).thenReturn(mockField)
      when(mockStateManager.reset(mockField)).thenReturn(true)

      val result = game.reset()
      result shouldBe true
      verify(mockStateManager).reset(mockField)
    }


//    "select defender position" in {
//      when(mockInitializer.selectDefenderPosition()).thenReturn(1)
//      game.selectDefenderPosition() shouldBe 1
//    }
  }
}
