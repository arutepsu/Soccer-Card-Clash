package de.htwg.se.soccercardclash.model.gameComponent.service
import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.{IHandCardsQueueFactory, *}
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.state.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IFieldCardsFactory, IGameCardsFactory, IHandCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import play.api.libs.json.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import org.mockito.ArgumentMatchers.{any, eq => meq}
import scala.xml.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.scalatest.wordspec.AnyWordSpec

class GameDeserializerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameDeserializer" should {

    val mockPlayerDeserializer = mock[PlayerDeserializer]
    val mockCardDeserializer = mock[CardDeserializer]
    val mockHandCardsQueueFactory = mock[IHandCardsQueueFactory]
    val mockHandCardsFactory = mock[IHandCardsFactory]
    val mockFieldCardsFactory = mock[IFieldCardsFactory]
    val mockGameCardsFactory = mock[IGameCardsFactory]
    val mockRolesFactory = mock[IRolesFactory]
    val mockScoresFactory = mock[IScoresFactory]

    val deserializer = new GameDeserializer(
      mockPlayerDeserializer,
      mockCardDeserializer,
      mockHandCardsQueueFactory,
      mockHandCardsFactory,
      mockFieldCardsFactory,
      mockGameCardsFactory,
      mockRolesFactory,
      mockScoresFactory
    )

    "correctly deserialize a simple XML game state" in {
      val xml: Elem =
        <Game>
          <attacker>
            <Player name="Alice" type="Human"/>
          </attacker>
          <defender>
            <Player name="Bob" type="Human"/>
          </defender>
          <attackerHand>
            <Card><value>5</value><suit>Hearts</suit><type>Regular</type></Card>
          </attackerHand>
          <defenderHand>
            <Card><value>9</value><suit>Clubs</suit><type>Regular</type></Card>
          </defenderHand>
          <attackerField></attackerField>
          <defenderField></defenderField>
          <attackerGoalkeeper/>
          <defenderGoalkeeper/>
          <attackerScore>3</attackerScore>
          <defenderScore>5</defenderScore>
        </Game>

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]

      when(mockPlayerDeserializer.fromXml(any[Elem])) thenReturn attacker thenReturn defender
      when(mockCardDeserializer.fromXml(any[Elem])) thenReturn card1 thenReturn card2
      when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(gameCards)
      when(mockRolesFactory.create(attacker, defender)).thenReturn(mock[IRoles])
      when(mockScoresFactory.createWithScores(any())).thenReturn(mock[IScores])

      val result = deserializer.fromXml(xml)

      result shouldBe a[IGameState]
    }

    "throw exception when missing attacker tag" in {
      val xml: Elem = <Game></Game>

      val ex = intercept[IllegalArgumentException] {
        deserializer.fromXml(xml)
      }

      ex.getMessage should include ("Missing <attacker> inner element")
    }

    "correctly deserialize a simple JSON game state" in {
      val json: JsObject = Json.obj(
        "attacker" -> Json.obj("name" -> "Alice", "type" -> "Human"),
        "defender" -> Json.obj("name" -> "Bob", "type" -> "Human"),
        "attackerHand" -> Json.arr(Json.obj("value" -> "5", "suit" -> "Hearts", "type" -> "Regular")),
        "defenderHand" -> Json.arr(Json.obj("value" -> "9", "suit" -> "Clubs", "type" -> "Regular")),
        "attackerField" -> Json.arr(JsNull, JsNull, JsNull),
        "defenderField" -> Json.arr(JsNull, JsNull, JsNull),
        "attackerGoalkeeper" -> Json.obj("value" -> "7", "suit" -> "Spades", "type" -> "Regular"),
        "defenderGoalkeeper" -> Json.obj("value" -> "8", "suit" -> "Diamonds", "type" -> "Regular"),
        "attackerScore" -> 3,
        "defenderScore" -> 5
      )

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val gk1 = mock[ICard]
      val gk2 = mock[ICard]
      val gameCards = mock[IGameCards]
      val scores = mock[IScores]
      val roles = mock[IRoles]

      when(mockPlayerDeserializer.fromJson(any[JsObject])) thenReturn attacker thenReturn defender
      when(mockCardDeserializer.fromJson(any[JsObject])) thenReturn card1 thenReturn card2 thenReturn gk1 thenReturn gk2
      when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(gameCards)
      when(mockRolesFactory.create(attacker, defender)).thenReturn(roles)
      when(mockScoresFactory.createWithScores(any())).thenReturn(scores)

      val result = deserializer.fromJson(json)

      result shouldBe a[IGameState]
    }
  }
}
