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

class FileIOSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockJsonComponent: JsonComponent = mock[JsonComponent]
  val mockXmlComponent: XmlComponent = mock[XmlComponent]
  val mockGameState: IGameState = mock[IGameState]
  val fileIO: IFileIO = new FileIO(mockJsonComponent, mockXmlComponent)

  "FileIO" should {

    "save the game using both XML and JSON components" in {
      noException should be thrownBy {
        fileIO.saveGame(mockGameState)
      }
      verify(mockXmlComponent).save(mockGameState)
      verify(mockJsonComponent).save(mockGameState)
    }

    "load a game from a JSON file successfully" in {
      when(mockJsonComponent.load(anyString())).thenReturn(Some(mockGameState))

      val result = fileIO.loadGame("savefile.json")
      result shouldBe mockGameState
    }

    "load a game from an XML file successfully" in {
      when(mockXmlComponent.load(anyString())).thenReturn(Some(mockGameState))

      val result = fileIO.loadGame("savefile.xml")
      result shouldBe mockGameState
    }

    "throw an exception for unsupported file formats" in {
      val exception = intercept[RuntimeException] {
        fileIO.loadGame("savefile.txt")
      }
      exception.getMessage should include ("Unsupported file format")
    }

    "throw an exception if game state is not found in JSON/XML" in {
      when(mockJsonComponent.load(anyString())).thenReturn(None)
      val exception = intercept[RuntimeException] {
        fileIO.loadGame("savefile.json")
      }
      exception.getMessage should include ("No valid save data found")
    }

    "throw an exception if saving fails" in {
      doThrow(new RuntimeException("Error")).when(mockXmlComponent).save(mockGameState)

      val exception = intercept[RuntimeException] {
        fileIO.saveGame(mockGameState)
      }
      exception.getMessage should include ("Error while saving")
    }

    "throw an exception if loading fails due to internal error" in {
      when(mockJsonComponent.load(anyString())).thenThrow(new RuntimeException("Disk error"))

      val exception = intercept[RuntimeException] {
        fileIO.loadGame("savefile.json")
      }
      exception.getMessage should include ("Error loading game")
    }
  }
}
