package de.htwg.se.soccercardclash.model.gameComponent

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.{Memento, Observable}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.*

import scala.xml.*

class GameStateSpec extends AnyFlatSpec with MockitoSugar {

  "IGameState" should "serialize to XML correctly" in {
    val attacker = mock[IPlayer]
    val defender = mock[IPlayer]
    val roles = mock[IRoles]
    val gameCards = mock[IGameCards]
    val scores = mock[IScores]
    val attackerHand = mock[IHandCardsQueue]
    val defenderHand = mock[IHandCardsQueue]
    val attackerCard = mock[ICard]
    val defenderCard = mock[ICard]
    val goalie = mock[ICard]

    when(roles.attacker).thenReturn(attacker)
    when(roles.defender).thenReturn(defender)

    when(attacker.toXml).thenReturn(<attacker/>)
    when(defender.toXml).thenReturn(<defender/>)

    when(attackerCard.toXml).thenReturn(<card>attacker</card>)
    when(defenderCard.toXml).thenReturn(<card>defender</card>)
    when(goalie.toXml).thenReturn(<goalie/>)

    when(attackerHand.toList).thenReturn(List(attackerCard))
    when(defenderHand.toList).thenReturn(List(defenderCard))

    when(gameCards.getPlayerHand(attacker)).thenReturn(attackerHand)
    when(gameCards.getPlayerHand(defender)).thenReturn(defenderHand)

    when(gameCards.getPlayerDefenders(attacker)).thenReturn(List(Some(attackerCard), None))
    when(gameCards.getPlayerDefenders(defender)).thenReturn(List(None, Some(defenderCard)))

    when(gameCards.getPlayerGoalkeeper(attacker)).thenReturn(Some(goalie))
    when(gameCards.getPlayerGoalkeeper(defender)).thenReturn(None)

    when(scores.getScore(attacker)).thenReturn(3)
    when(scores.getScore(defender)).thenReturn(5)

    val gameState = GameState(gameCards, roles, scores)

    val xml = gameState.toXml

    (xml \ "attacker").nonEmpty shouldBe true
    (xml \ "defender").nonEmpty shouldBe true
    (xml \ "attackerHand" \ "card").size shouldBe 1
    (xml \ "defenderHand" \ "card").size shouldBe 1
    (xml \ "attackerField" \ "card").size shouldBe 1
    (xml \ "attackerField" \ "card").size shouldBe 1
    (xml \ "defenderField" \ "card").size shouldBe 1
    (xml \ "defenderField" \ "card").size shouldBe 1
    (xml \ "attackerGoalkeeper" \ "goalie").nonEmpty shouldBe true
    (xml \ "defenderGoalkeeper" \ "empty").nonEmpty shouldBe true
    (xml \ "attackerScore").text.trim shouldBe "3"
    (xml \ "defenderScore").text.trim shouldBe "5"
  }

  it should "serialize to JSON correctly" in {
    val attacker = mock[IPlayer]
    val defender = mock[IPlayer]
    val roles = mock[IRoles]
    val gameCards = mock[IGameCards]
    val scores = mock[IScores]
    val attackerHand = mock[IHandCardsQueue]
    val defenderHand = mock[IHandCardsQueue]
    val attackerCard = mock[ICard]
    val defenderCard = mock[ICard]
    val goalie = mock[ICard]

    when(roles.attacker).thenReturn(attacker)
    when(roles.defender).thenReturn(defender)

    when(attacker.toJson).thenReturn(Json.obj("attacker" -> true))
    when(defender.toJson).thenReturn(Json.obj("defender" -> true))

    when(attackerCard.toJson).thenReturn(Json.obj("card" -> "attacker"))
    when(defenderCard.toJson).thenReturn(Json.obj("card" -> "defender"))
    when(goalie.toJson).thenReturn(Json.obj("goalie" -> true))


    when(attackerHand.toList).thenReturn(List(attackerCard))
    when(defenderHand.toList).thenReturn(List(defenderCard))

    when(gameCards.getPlayerHand(attacker)).thenReturn(attackerHand)
    when(gameCards.getPlayerHand(defender)).thenReturn(defenderHand)

    when(gameCards.getPlayerDefenders(attacker)).thenReturn(List(Some(attackerCard), None))
    when(gameCards.getPlayerDefenders(defender)).thenReturn(List(None, Some(defenderCard)))

    when(gameCards.getPlayerGoalkeeper(attacker)).thenReturn(Some(goalie))
    when(gameCards.getPlayerGoalkeeper(defender)).thenReturn(None)

    when(scores.getScore(attacker)).thenReturn(1)
    when(scores.getScore(defender)).thenReturn(2)

    val gameState = GameState(gameCards, roles, scores)

    val json = gameState.toJson

    (json \ "attacker").as[JsObject] shouldBe Json.obj("attacker" -> true)
    (json \ "defender").as[JsObject] shouldBe Json.obj("defender" -> true)
    (json \ "attackerHand")(0) shouldBe Json.obj("card" -> "attacker")
    (json \ "defenderHand")(0) shouldBe Json.obj("card" -> "defender")
    (json \ "attackerField")(0) shouldBe Json.obj("card" -> "attacker")
    (json \ "attackerField")(1) shouldBe JsNull
    (json \ "defenderField")(0) shouldBe JsNull
    (json \ "defenderField")(1) shouldBe Json.obj("card" -> "defender")
    (json \ "attackerGoalkeeper").get shouldBe Json.obj("goalie" -> true)
    (json \ "defenderGoalkeeper").get shouldBe Json.obj()
    (json \ "attackerScore").as[Int] shouldBe 1
    (json \ "defenderScore").as[Int] shouldBe 2
  }
  "GameState" should "create and restore from valid GameStateMemento" in {
    val gameCards = mock[IGameCards]
    val roles = mock[IRoles]
    val scores = mock[IScores]

    val state = GameState(gameCards, roles, scores)
    val memento = state.createMemento()

    val restored = state.restoreFromMemento(memento)

    assert(restored.isInstanceOf[GameState])
    val restoredState = restored.asInstanceOf[GameState]
    assert(restoredState.gameCards eq gameCards)
    assert(restoredState.roles eq roles)
    assert(restoredState.scores eq scores)
  }

  it should "throw IllegalArgumentException on invalid memento" in {
    val gameCards = mock[IGameCards]
    val roles = mock[IRoles]
    val scores = mock[IScores]

    val state = GameState(gameCards, roles, scores)

    val invalidMemento = new Memento {}

    assertThrows[IllegalArgumentException] {
      state.restoreFromMemento(invalidMemento)
    }
  }
}
