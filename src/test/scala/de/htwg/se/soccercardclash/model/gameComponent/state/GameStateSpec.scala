package de.htwg.se.soccercardclash.model.gameComponent.state

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, IScores}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.Observable
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

    val gameState = new IGameState {
      override def getGameCards: IGameCards = gameCards

      override def getRoles: IRoles = roles

      override def getScores: IScores = scores

      override def newGameCards(newGameCards: IGameCards): IGameState = this

      override def newRoles(newRoles: IRoles): IGameState = this

      override def newScores(newScores: IScores): IGameState = this
    }

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

    val gameState = new IGameState {
      override def getGameCards: IGameCards = gameCards

      override def getRoles: IRoles = roles

      override def getScores: IScores = scores

      override def newGameCards(newGameCards: IGameCards): IGameState = this

      override def newRoles(newRoles: IRoles): IGameState = this

      override def newScores(newScores: IScores): IGameState = this
    }

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
}
