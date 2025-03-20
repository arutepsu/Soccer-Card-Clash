package model.gameComponent.io

import model.fileIOComponent.IFileIO
import model.gameComponent.state.IGameState
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.Failure

class GamePersistenceSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GamePersistence" should {

    val mockFileIO = mock[IFileIO]
    val mockState = mock[IGameState]
    val persistence = new GamePersistence(mockFileIO)

    "save game successfully" in {
      noException should be thrownBy persistence.saveGame(mockState)
      verify(mockFileIO).saveGame(mockState)
    }

    "throw exception when saveGame fails" in {
      val errorIO = mock[IFileIO]
      val failingPersistence = new GamePersistence(errorIO)

      when(errorIO.saveGame(any())).thenThrow(new RuntimeException("Disk full"))

      val ex = intercept[RuntimeException] {
        failingPersistence.saveGame(mockState)
      }

      ex.getMessage should include("Failed to save the game")
    }

    "load game successfully" in {
      when(mockFileIO.loadGame("game.json")).thenReturn(mockState)

      val result = persistence.loadGame("game.json")
      result shouldBe Some(mockState)
    }

    "return None when loadGame returns null" in {
      when(mockFileIO.loadGame("missing.json")).thenReturn(null)

      val result = persistence.loadGame("missing.json")
      result shouldBe None
    }

    "return None if loadGame throws exception" in {
      val faultyIO = mock[IFileIO]
      val failingPersistence = new GamePersistence(faultyIO)

      when(faultyIO.loadGame(any())).thenThrow(new RuntimeException("read error"))

      val result = failingPersistence.loadGame("broken.json")
      result shouldBe None
    }
  }
}
