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
  }
}
