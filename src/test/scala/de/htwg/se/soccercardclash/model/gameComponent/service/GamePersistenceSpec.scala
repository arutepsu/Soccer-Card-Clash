package de.htwg.se.soccercardclash.model.gameComponent.service

import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.gameComponent.service.GamePersistence
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.{Failure, Success}

class GamePersistenceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GamePersistence" should {

    val mockFileIO = mock[IFileIO]
    val mockState = mock[IGameState]
    val persistence = new GamePersistence(mockFileIO)

    "save game successfully" in {
      when(mockFileIO.saveGame(mockState)).thenReturn(Success(()))

      val result = persistence.saveGame(mockState)

      result shouldBe Success(())
      verify(mockFileIO).saveGame(mockState)
    }

    "return Failure when saveGame fails" in {
      val error = new RuntimeException("Disk full")
      when(mockFileIO.saveGame(mockState)).thenReturn(Failure(error))

      val result = persistence.saveGame(mockState)

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Disk full")
    }

    "load game successfully" in {
      when(mockFileIO.loadGame("game.json")).thenReturn(Success(mockState))

      val result = persistence.loadGame("game.json")

      result shouldBe Success(mockState)
    }

    "return Failure when loadGame returns error" in {
      val error = new RuntimeException("File not found")
      when(mockFileIO.loadGame("missing.json")).thenReturn(Failure(error))

      val result = persistence.loadGame("missing.json")

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("File not found")
    }
  }
}