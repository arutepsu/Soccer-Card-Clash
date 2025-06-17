package de.htwg.se.soccercardclash.model.playerComponent.base

import de.htwg.se.soccercardclash.model.playerComponent.ai.types.TakaStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.*

import scala.xml.Utility.trim

class PlayerSerializationSpec extends AnyWordSpec with Matchers {

  val actionStates: Map[PlayerActionPolicies, PlayerActionState] =
    Map(
      PlayerActionPolicies.Boost -> CanPerformAction(2),
      PlayerActionPolicies.DoubleAttack -> CanPerformAction(1)
    )

  "A Human Player" should {
    val player = Player("Alice", actionStates, Human)

    "serialize to correct JSON" in {
      val expectedJson = Json.obj(
        "name" -> "Alice",
        "type" -> "Human",
        "actionStates" -> Json.obj(
          "Boost" -> "CanPerformAction(2)",
          "DoubleAttack" -> "CanPerformAction(1)"
        )
      )

      player.toJson shouldBe expectedJson
    }

    "serialize to correct XML" in {
      val xml = player.toXml

      (xml \ "@name").text shouldBe "Alice"
      (xml \ "@type").text shouldBe "Human"
      (xml \ "@strategy").text shouldBe ""

      val states = (xml \ "ActionStates" \ "ActionState").map(node =>
        (node \ "@policy").text -> node.text.trim
      ).toMap

      states should contain("Boost" -> "CanPerformAction(2)")
      states should contain("DoubleAttack" -> "CanPerformAction(1)")
    }
  }

  "An AI Player with TakaStrategy" should {
    val actionStates: Map[PlayerActionPolicies, PlayerActionState] = Map(
      PlayerActionPolicies.Boost -> CanPerformAction(2),
      PlayerActionPolicies.DoubleAttack -> CanPerformAction(1)
    )

    val randomProvider = mock[IRandomProvider]
    val strategy = new TakaStrategy(randomProvider)
    val player = Player("Bot", actionStates, AI(strategy))

    "serialize to correct JSON" in {
      val json = player.toJson

      (json \ "name").as[String] shouldBe "Bot"
      (json \ "type").as[String] shouldBe "AI"
      (json \ "strategy").as[String] shouldBe "TakaStrategy"

      val actions = (json \ "actionStates").as[JsObject].value.map {
        case (k, v) => k -> v.as[String]
      }

      actions should contain("Boost" -> "CanPerformAction(2)")
      actions should contain("DoubleAttack" -> "CanPerformAction(1)")
    }

    "serialize to correct XML" in {
      val xml = player.toXml

      (xml \ "@name").text shouldBe "Bot"
      (xml \ "@type").text shouldBe "AI"
      (xml \ "@strategy").text shouldBe "TakaStrategy"

      val states = (xml \ "ActionStates" \ "ActionState").map { node =>
        (node \ "@policy").text -> node.text.trim
      }.toMap

      states should contain("Boost" -> "CanPerformAction(2)")
      states should contain("DoubleAttack" -> "CanPerformAction(1)")
    }
  }
}

