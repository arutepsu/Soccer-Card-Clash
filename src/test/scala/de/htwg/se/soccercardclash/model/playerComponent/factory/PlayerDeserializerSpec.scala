package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human, Player}
import de.htwg.se.soccercardclash.model.playerComponent.factory.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.*

import scala.xml.Elem
import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.{DefendraStrategy, TakaStrategy}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}

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

      val mockRandomProvider = mock[IRandomProvider]
      val deserializer = new PlayerDeserializer(
        mock[CardDeserializer],
        Map("Taka" -> mockRandomProvider)
      )

      val result = deserializer.fromXml(xml)

      result.name shouldBe "Bot"
      result.playerType match {
        case AI(strategy: TakaStrategy) =>
          strategy.random shouldBe mockRandomProvider
        case other =>
          fail(s"Expected AI with TakaStrategy, but got: $other")
      }

      result.actionStates shouldBe Map(
        PlayerActionPolicies.Boost -> CanPerformAction(2),
        PlayerActionPolicies.DoubleAttack -> CanPerformAction(2),
        PlayerActionPolicies.Swap -> OutOfActions
      )

    }


    "correctly deserialize an AI Player from JSON with strategy" in {
      val json = Json.obj(
        "name" -> "Bot",
        "type" -> "AI",
        "strategy" -> "DefendraStrategy",
        "actionStates" -> Json.obj(
          "Boost" -> "OutOfActions",
          "Swap" -> "CanPerformAction(1)")
      )

      val mockRandomProvider = mock[IRandomProvider]
      val deserializer = new PlayerDeserializer(
        mock[CardDeserializer],
        Map("Defendra" -> mockRandomProvider)
      )

      val result = deserializer.fromJson(json)

      result.name shouldBe "Bot"
      result.playerType match {
        case AI(strategy: DefendraStrategy) =>
          strategy.random shouldBe mockRandomProvider
        case other =>
          fail(s"Expected AI with DefendraStrategy, but got: $other")
      }

      result.actionStates shouldBe Map(
        PlayerActionPolicies.Boost -> OutOfActions,
        PlayerActionPolicies.DoubleAttack -> CanPerformAction(1),
        PlayerActionPolicies.Swap -> CanPerformAction(1)
      )

    }


    "throw exception when XML is missing name" in {
      val xml: Elem = <Player></Player>
      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromXml(xml)
      }

      ex.getMessage should include("Missing player 'name' attribute")
    }

    "throw exception when JSON is missing name" in {
      val json: JsObject = Json.obj("type" -> "AI")
      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }
      ex.getMessage should include("Error parsing Player JSON")
    }

    "throw exception when XML contains unknown policy" in {
      val xml: Elem =
        <Player name="John" type="Human">
          <ActionStates>
            <ActionState policy="Fly">OutOfActions</ActionState>
          </ActionStates>
        </Player>

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromXml(xml)
      }
      ex.getMessage should include("Unknown policy: Fly")
    }

    "throw exception when XML has invalid action state string" in {
      val xml: Elem =
        <Player name="John" type="Human">
          <ActionStates>
            <ActionState policy="Boost">InvalidState</ActionState>
          </ActionStates>
        </Player>

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromXml(xml)
      }
      ex.getMessage should include("InvalidState")
    }

    "throw exception when JSON contains unknown policy" in {
      val json: JsObject = Json.obj(
        "name" -> "Alice",
        "actionStates" -> Json.obj("Teleport" -> "OutOfActions")
      )

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }
      ex.getMessage should include("Unknown policy: Teleport")
    }

    "throw exception when JSON contains invalid action state string" in {
      val json: JsObject = Json.obj(
        "name" -> "Bob",
        "actionStates" -> Json.obj("Boost" -> "InvalidState")
      )

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }
      ex.getMessage should include("InvalidState")
    }

    "deserialize player with no cards and no actions" in {
      val xml: Elem =
        <Player name="Jake" type="Human">
          <ActionStates></ActionStates>
        </Player>

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)
      val result = deserializer.fromXml(xml)

      result.name shouldBe "Jake"
      result.playerType shouldBe Human
      result.actionStates shouldBe Map.empty
    }

    "throw exception when XML has unknown player type" in {
      val xml: Elem =
        <Player name="Ghost" type="Alien">
          <ActionStates></ActionStates>
        </Player>

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromXml(xml)
      }

      ex.getMessage.toLowerCase should include("unknown player type")
    }
    "throw exception when JSON has unknown player type" in {
      val json: JsObject = Json.obj(
        "name" -> "Ghost",
        "type" -> "Alien"
      )

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }

      ex.getMessage.toLowerCase should include("unknown player type")
    }
    "throw exception when AI strategy is unknown in JSON" in {
      val json = Json.obj(
        "name" -> "Bot",
        "type" -> "AI",
        "strategy" -> "UnknownStrategy",
        "actionStates" -> Json.obj("Boost" -> "CanPerformAction(1)")
      )

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)

      val ex = intercept[RuntimeException] {
        deserializer.fromJson(json)
      }

      ex.getMessage should include("Unsupported AI strategy: UnknownStrategy")
    }
    "correctly deserialize a Human Player from JSON" in {
      val json = Json.obj(
        "name" -> "Alice",
        "type" -> "Human",
        "actionStates" -> Json.obj(
          "DoubleAttack" -> "CanPerformAction(2)",
          "Swap" -> "OutOfActions"
        )
      )

      val deserializer = new PlayerDeserializer(mock[CardDeserializer], Map.empty)
      val result = deserializer.fromJson(json)

      result.name shouldBe "Alice"
      result.playerType shouldBe Human
      result.actionStates shouldBe Map(
        PlayerActionPolicies.DoubleAttack -> CanPerformAction(2),
        PlayerActionPolicies.Swap -> OutOfActions
      )
    }
  }
}
