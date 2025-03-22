package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}
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
  "throw exception when XML contains unknown policy" in {
    val xml: Elem =
      <Player name="John">
        <Cards>
          <Card>
            <value>5</value> <suit>Hearts</suit> <type>Regular</type>
          </Card>
        </Cards>
        <ActionStates>
          <ActionState policy="Fly">OutOfActions</ActionState>
        </ActionStates>
      </Player>

    val mockFactory = mock[IPlayerFactory]
    val mockCardDeserializer = mock[CardDeserializer]
    when(mockCardDeserializer.fromXml(any[Elem])).thenReturn(mock[ICard])

    val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)

    val ex = intercept[RuntimeException] {
      deserializer.fromXml(xml)
    }

    ex.getMessage should include("Unknown policy: Fly")
  }
  "throw exception when XML has invalid action state string" in {
    val xml: Elem =
      <Player name="John">
        <Cards>
          <Card><value>5</value><suit>Hearts</suit><type>Regular</type></Card>
        </Cards>
        <ActionStates>
          <ActionState policy="Boost">NotAValidState</ActionState>
        </ActionStates>
      </Player>

    val mockFactory = mock[IPlayerFactory]
    val mockCardDeserializer = mock[CardDeserializer]
    when(mockCardDeserializer.fromXml(any[Elem])).thenReturn(mock[ICard])

    val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)

    val ex = intercept[RuntimeException] {
      deserializer.fromXml(xml)
    }

    ex.getMessage should include ("NotAValidState")
  }
  "throw exception when JSON contains unknown policy" in {
    val json: JsObject = Json.obj(
      "name" -> "Alice",
      "cards" -> Json.arr(),
      "actionStates" -> Json.obj("Teleport" -> "OutOfActions")
    )

    val deserializer = new PlayerDeserializer(mock[IPlayerFactory], mock[CardDeserializer])

    val ex = intercept[RuntimeException] {
      deserializer.fromJson(json)
    }

    ex.getMessage should include ("Unknown policy: Teleport")
  }
  "throw exception when JSON contains invalid action state string" in {
    val json: JsObject = Json.obj(
      "name" -> "Bob",
      "cards" -> Json.arr(),
      "actionStates" -> Json.obj("Boost" -> "InvalidState")
    )

    val deserializer = new PlayerDeserializer(mock[IPlayerFactory], mock[CardDeserializer])

    val ex = intercept[RuntimeException] {
      deserializer.fromJson(json)
    }

    ex.getMessage should include ("InvalidState")
  }
  "throw exception when card deserialization fails in XML" in {
    val xml: Elem =
      <Player name="Jake">
        <Cards>
          <Card><value>999</value><suit>Imaginary</suit><type>Regular</type></Card>
        </Cards>
        <ActionStates></ActionStates>
      </Player>

    val mockFactory = mock[IPlayerFactory]
    val mockCardDeserializer = mock[CardDeserializer]
    when(mockCardDeserializer.fromXml(any[Elem]))
      .thenThrow(new IllegalArgumentException("Invalid card"))

    val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)

    val ex = intercept[RuntimeException] {
      deserializer.fromXml(xml)
    }

    ex.getMessage should include ("Invalid card")
  }

}
