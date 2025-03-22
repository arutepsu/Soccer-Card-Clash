package model.playerComponent.factory

import model.cardComponent.ICard
import model.cardComponent.factory.CardDeserializer
import model.playerComponent.IPlayer
import model.playerComponent.base.Player
import model.playerComponent.factory.IPlayerFactory
import model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json._
import scala.xml.Elem
import org.mockito.ArgumentMatchers.any

class PlayerDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayerDeserializer" should {

    "correctly deserialize a Player from XML" in {
      val xml: Elem =
        <Player name="John">
          <Cards>
            <Card><value>5</value><suit>Hearts</suit><type>Regular</type></Card>
          </Cards>
          <ActionStates>
            <ActionState policy="Boost">OutOfActions</ActionState>
          </ActionStates>
        </Player>

      val mockCard = mock[ICard]
      val mockPlayer = mock[IPlayer]
      val mockFactory = mock[IPlayerFactory]
      val mockCardDeserializer = mock[CardDeserializer]

      when(mockCardDeserializer.fromXml(any[Elem])).thenReturn(mockCard)
      when(mockFactory.createPlayer("John", List(mockCard))).thenReturn(mockPlayer)
      when(mockPlayer.setActionStates(Map(PlayerActionPolicies.Boost -> OutOfActions))).thenReturn(mockPlayer)

      val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)
      val result = deserializer.fromXml(xml)

      result shouldBe mockPlayer
    }

    "correctly deserialize a Player from JSON" in {
      val json: JsObject = Json.obj(
        "name" -> "Alice",
        "cards" -> Json.arr(Json.obj("value" -> "7", "suit" -> "Clubs", "type" -> "Regular")),
        "actionStates" -> Json.obj(
          "Swap" -> "CanPerformAction(2)"
        )
      )

      val mockCard = mock[ICard]
      val mockPlayer = mock[IPlayer]
      val mockFactory = mock[IPlayerFactory]
      val mockCardDeserializer = mock[CardDeserializer]

      when(mockCardDeserializer.fromJson(any[JsObject])).thenReturn(mockCard)
      when(mockFactory.createPlayer("Alice", List(mockCard))).thenReturn(mockPlayer)
      when(mockPlayer.setActionStates(Map(PlayerActionPolicies.Swap -> CanPerformAction(2)))).thenReturn(mockPlayer)

      val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)
      val result = deserializer.fromJson(json)

      result shouldBe mockPlayer
    }

    "throw exception when XML is missing name" in {
      val xml: Elem =
        <Player>
          <Cards></Cards>
          <ActionStates></ActionStates>
        </Player>

      val deserializer = new PlayerDeserializer(mock[IPlayerFactory], mock[CardDeserializer])

      val ex = intercept[RuntimeException] {
        deserializer.fromXml(xml)
      }

      ex.getMessage should include ("Missing 'name'")
    }

    "throw exception when JSON is missing name" in {
      val json: JsObject = Json.obj(
        "cards" -> Json.arr(),
        "actionStates" -> Json.obj()
      )

      val deserializer = new PlayerDeserializer(mock[IPlayerFactory], mock[CardDeserializer])

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }

      ex.getMessage should include ("Error parsing Player JSON")
    }
  }
}
