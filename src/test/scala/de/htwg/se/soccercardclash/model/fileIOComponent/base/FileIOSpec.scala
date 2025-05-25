package de.htwg.se.soccercardclash.model.fileIOComponent.base

import de.htwg.se.soccercardclash.model.fileIOComponent.IFileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.base.FileIO
import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.fileIOComponent.xmlComponent.XmlComponent
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.anyString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import scala.util.{Failure, Success}

class FileIOSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockJsonComponent: JsonComponent = mock[JsonComponent]
  val mockXmlComponent: XmlComponent = mock[XmlComponent]
  val mockGameState: IGameState = mock[IGameState]
  val fileIO: IFileIO = new FileIO(mockJsonComponent, mockXmlComponent)

  "FileIO" should {

    "save the game using both XML and JSON components" in {
      // Arrange
      doNothing().when(mockXmlComponent).save(mockGameState)
      doNothing().when(mockJsonComponent).save(mockGameState)

      // Act
      val result = fileIO.saveGame(mockGameState)

      // Assert
      result shouldBe Success(())
      verify(mockXmlComponent).save(mockGameState)
      verify(mockJsonComponent).save(mockGameState)
    }

    "load a game from a JSON file successfully" in {
      when(mockJsonComponent.load("savefile.json")).thenReturn(Some(mockGameState))

      val result = fileIO.loadGame("savefile.json")

      result shouldBe Success(mockGameState)
    }

    "load a game from an XML file successfully" in {
      when(mockXmlComponent.load("savefile.xml")).thenReturn(Some(mockGameState))

      val result = fileIO.loadGame("savefile.xml")

      result shouldBe Success(mockGameState)
    }

    "return Failure for unsupported file formats" in {
      val result = fileIO.loadGame("savefile.txt")

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Unsupported file format")
    }

    "return Failure if game state is not found in JSON/XML" in {
      when(mockJsonComponent.load("savefile.json")).thenReturn(None)

      val result = fileIO.loadGame("savefile.json")

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("No valid save data found")
    }

    "return Failure if saving fails" in {
      doThrow(new RuntimeException("Error")).when(mockXmlComponent).save(mockGameState)

      val result = fileIO.saveGame(mockGameState)

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Error")
    }

    "return Failure if loading fails due to internal error" in {
      when(mockJsonComponent.load("savefile.json")).thenThrow(new RuntimeException("Disk error"))

      val result = fileIO.loadGame("savefile.json")

      result.isFailure shouldBe true
      result.failed.get.getMessage should include("Disk error")
    }
  }
}