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
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import org.mockito.ArgumentMatchers.{any, eq => eqTo}

class PlayerDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayerDeserializer" should {
    "correctly deserialize an AI Player from XML with strategy" in {
      val xml: Elem =
        <Player name="Bot" type="AI" strategy="TakaStrategy">
          <ActionStates>
            <ActionState policy="DoubleAttack">CanPerformAction(2)</ActionState>
            <ActionState policy="Swap">OutOfActions</ActionState>
          </ActionStates>
        </Player>

      val mockFactory = mock[IPlayerFactory]
      val mockCardDeserializer = mock[CardDeserializer]
      val mockPlayer = mock[IPlayer]

      val expectedLimits = Map(
        PlayerActionPolicies.DoubleAttack -> 2,
        PlayerActionPolicies.Swap -> 0
      )

      when(mockFactory.createAIPlayer(eqTo("Bot"), any[IAIStrategy], eqTo(expectedLimits)))
      .thenReturn(mockPlayer)


      val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)
      val result = deserializer.fromXml(xml)

      result shouldBe mockPlayer
    }

    "correctly deserialize an AI Player from JSON with strategy" in {
      val json = Json.obj(
        "name" -> "Bot",
        "type" -> "AI",
        "strategy" -> "DefendraStrategy",
        "actionStates" -> Json.obj(
          "Boost" -> "OutOfActions",
          "Swap" -> "CanPerformAction(1)"
        )
      )

      val mockFactory = mock[IPlayerFactory]
      val mockCardDeserializer = mock[CardDeserializer]
      val mockPlayer = mock[IPlayer]

      val expectedLimits = Map(
        PlayerActionPolicies.Boost -> 0,
        PlayerActionPolicies.Swap -> 1
      )

      when(mockFactory.createAIPlayer(eqTo("Bot"), any[IAIStrategy], eqTo(expectedLimits)))
      .thenReturn(mockPlayer)


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

      ex.getMessage should include ("Missing player 'name' attribute")
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
      <Player name="John" type="Human">
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
      <Player name="John" type="Human">
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
  "deserialize player with no cards and no actions" in {
    val xml: Elem =
      <Player name="Jake" type="Human">
        <ActionStates></ActionStates>
      </Player>

    val mockFactory = mock[IPlayerFactory]
    val mockCardDeserializer = mock[CardDeserializer]
    val mockPlayer = mock[IPlayer]

    when(mockFactory.createPlayer("Jake")).thenReturn(mockPlayer)
    when(mockPlayer.setActionStates(Map.empty)).thenReturn(mockPlayer)

    val deserializer = new PlayerDeserializer(mockFactory, mockCardDeserializer)

    val result = deserializer.fromXml(xml)

    result shouldBe mockPlayer
  }

}