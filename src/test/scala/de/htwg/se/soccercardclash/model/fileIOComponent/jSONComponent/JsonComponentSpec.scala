package de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent

import de.htwg.se.soccercardclash.model.fileIOComponent.jSONComponent.JsonComponent
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.factory.GameDeserializer
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsObject, Json}

import java.io.{File, PrintWriter}
import scala.util.Using
import scala.io.Source

class JsonComponentSpec extends AnyWordSpec with Matchers with MockitoSugar {

  val mockDeserializer: GameDeserializer = mock[GameDeserializer]
  val mockGameState: IGameState = mock[IGameState]

  val jsonComponent = new JsonComponent(mockDeserializer)

  "JsonComponent" should {

    "save game state to file" in {
      val dummyJson: JsObject = Json.obj("game" -> "test")
      when(mockGameState.toJson).thenReturn(dummyJson)

      jsonComponent.save(mockGameState)

      val saved = Source.fromFile("games/game.json").getLines().mkString
      saved should include("game")
      saved should include("test")
    }

    "load game state from file if file exists" in {
      val dummyJson = Json.obj("game" -> "test")
      val dummyJsonString = Json.prettyPrint(dummyJson)

      // Write dummy JSON to file
      Using(new PrintWriter(new File("games/test.json"))) { pw =>
        pw.write(dummyJsonString)
      }

      when(mockDeserializer.fromJson(any())).thenReturn(mockGameState)

      val result = jsonComponent.load("test.json")
      result shouldBe Some(mockGameState)
    }

    "return None if file does not exist" in {
      val result = jsonComponent.load("non_existing_file.json")
      result shouldBe None
    }

    "return None if deserialization fails" in {
      val dummyJson = Json.obj("broken" -> "data")
      val brokenContent = Json.prettyPrint(dummyJson)

      Using(new PrintWriter(new File("games/broken.json"))) { pw =>
        pw.write(brokenContent)
      }

      when(mockDeserializer.fromJson(any())).thenThrow(new RuntimeException("boom"))

      val result = jsonComponent.load("broken.json")
      result shouldBe None
    }
    "not throw if saving fails due to file write error" in {
      val gameState = mock[IGameState]
      when(gameState.toJson).thenReturn(Json.obj("game" -> "test"))

      // Temporarily make `games/` a file instead of folder
      val gamesPath = new File("games")
      gamesPath.delete()
      Using(new PrintWriter(gamesPath))(_.write("not_a_directory"))

      noException should be thrownBy {
        jsonComponent.save(gameState)
      }

      // Clean up
      gamesPath.delete()
    }
    "create games folder if it does not exist" in {
      val folder = new File("games")
      if (folder.exists()) folder.delete()

      val dummyJson: JsObject = Json.obj("game" -> "test")
      when(mockGameState.toJson).thenReturn(dummyJson)

      jsonComponent.save(mockGameState)

      folder.exists() shouldBe true
      folder.isDirectory shouldBe true
    }
    "return None if file content is not valid JSON" in {
      val badJsonFile = new File("games/invalid.json")
      Using(new PrintWriter(badJsonFile)) { pw =>
        pw.write("not-a-json-string")
      }

      val result = jsonComponent.load("invalid.json")
      result shouldBe None
    }
    "load" should {

      "return None if file does not exist" in {
        val jsonComponent = new JsonComponent(mockDeserializer)
        val result = jsonComponent.load("non_existing_file.json")
        result shouldBe None
      }

      "return None if deserialization fails due to bad JSON" in {
        // Arrange: Create a temp file with invalid JSON
        val badFileName = "invalid.json"
        val folder = new File("games")
        if (!folder.exists()) folder.mkdir()

        val writer = new PrintWriter(new File(s"games/$badFileName"))
        writer.write("{ bad json }") // intentionally invalid JSON
        writer.close()

        // Act: This should fail parsing or deserialization
        val component = new JsonComponent(mockDeserializer)
        val result = component.load(badFileName)

        // Assert: load returns None
        result shouldBe None

        // Cleanup
        new File(s"games/$badFileName").delete()
      }
    }

  }
}
