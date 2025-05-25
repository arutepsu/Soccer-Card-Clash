package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import scala.util.{Success, Failure, Try}

class GameServiceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameService" should {

    "create a new game with two players" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]
      val state = mock[IGameState]

      when(initializer.createGameState("Alice", "Bob")).thenReturn(state)

      val service = new GameService(initializer, persistence, Map.empty)

      service.createNewGame("Alice", "Bob") shouldBe state
    }

    "create a new game with AI from roster" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]
      val state = mock[IGameState]
      val aiPlayer = mock[IPlayer]

      val service = new GameService(initializer, persistence, Map("AI-Bot" -> aiPlayer))

      when(initializer.createGameStateWithAI("Alice", aiPlayer)).thenReturn(state)

      service.createNewGameWithAI("Alice", "AI-Bot") shouldBe state
    }

    "throw exception for unknown AI" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]

      val service = new GameService(initializer, persistence, Map.empty)

      intercept[IllegalArgumentException] {
        service.createNewGameWithAI("Alice", "UnknownAI")
      }.getMessage should include ("Unknown AI")
    }

    "load a saved game and reinitialize state" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]
      val rawState = mock[IGameState]
      val finalState = mock[IGameState]

      when(persistence.loadGame("file1")).thenReturn(Success(rawState))
      when(initializer.initializeFromState(rawState)).thenReturn(finalState)

      val service = new GameService(initializer, persistence, Map.empty)

      service.loadGame("file1") shouldBe Success(finalState)
    }

    "return failure if loading fails" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]

      val ex = new RuntimeException("failed")
      when(persistence.loadGame("file2")).thenReturn(Failure(ex))

      val service = new GameService(initializer, persistence, Map.empty)

      val result = service.loadGame("file2")
      result.isFailure shouldBe true
    }

    "save the game state" in {
      val initializer = mock[IGameInitializer]
      val persistence = mock[IGamePersistence]
      val state = mock[IGameState]

      when(persistence.saveGame(state)).thenReturn(Success(()))

      val service = new GameService(initializer, persistence, Map.empty)

      service.saveGame(state) shouldBe Success(())
    }
  }
}
