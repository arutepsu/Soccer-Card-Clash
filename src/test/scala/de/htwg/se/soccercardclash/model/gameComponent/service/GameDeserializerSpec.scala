package de.htwg.se.soccercardclash.model.gameComponent.service
import com.google.inject.{Inject, Singleton}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.base.GameState
import de.htwg.se.soccercardclash.model.gameComponent.components.{IFieldCardsFactory, IGameCardsFactory, IHandCardsFactory, IRolesFactory, IScoresFactory}
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import play.api.libs.json.*
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.ArgumentMatchers.eq
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
    "fromXml" should {

      "correctly deserialize well-formed XML with full data" in {
        val xml =
          <Game>
            <attacker><Player name="A" type="Human"/></attacker>
            <defender><Player name="B" type="Human"/></defender>
            <attackerHand>
              <Card><value>5</value><suit>Hearts</suit><type>Regular</type></Card>
            </attackerHand>
            <defenderHand>
              <Card><value>9</value><suit>Clubs</suit><type>Regular</type></Card>
            </defenderHand>
            <attackerField>
              <Card xsi:nil="true"/>
              <Card><value>7</value><suit>Spades</suit><type>Regular</type></Card>
            </attackerField>
            <defenderField></defenderField>
            <attackerGoalkeeper><Card><value>6</value><suit>Hearts</suit><type>Regular</type></Card></attackerGoalkeeper>
            <defenderGoalkeeper><Card><value>4</value><suit>Diamonds</suit><type>Regular</type></Card></defenderGoalkeeper>
            <attackerScore>2</attackerScore>
            <defenderScore>3</defenderScore>
          </Game>

        val attacker = mock[IPlayer]
        val defender = mock[IPlayer]
        val card1 = mock[ICard]
        val card2 = mock[ICard]
        val gk1 = mock[ICard]
        val gk2 = mock[ICard]
        val gameCards = mock[IGameCards]
        val scores = mock[IScores]
        val roles = mock[IRoles]

        when(mockPlayerDeserializer.fromXml(any[Elem])) thenReturn attacker thenReturn defender
        when(mockCardDeserializer.fromXml(any[Elem])) thenReturn card1 thenReturn card2 thenReturn gk1 thenReturn gk2
        when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(gameCards)
        when(mockRolesFactory.create(attacker, defender)).thenReturn(roles)
        when(mockScoresFactory.createWithScores(any())).thenReturn(scores)

        val result = deserializer.fromXml(xml)
        result shouldBe a[IGameState]
      }

      "throw exception if attacker element is missing" in {
        val xml = <Game><defender><Player name="B" type="Human"/></defender></Game>
        val ex = intercept[IllegalArgumentException](deserializer.fromXml(xml))
        ex.getMessage should include ("Missing <attacker> inner element")
      }

      "throw exception if defender element is missing" in {
        val xml = <Game><attacker><Player name="A" type="Human"/></attacker></Game>
        val ex = intercept[IllegalArgumentException](deserializer.fromXml(xml))
        ex.getMessage should include ("Missing <defender> inner element")
      }

      "treat missing cards and nil fields as empty" in {
        val xml =
          <Game>
            <attacker><Player name="A" type="Human"/></attacker>
            <defender><Player name="B" type="Human"/></defender>
            <attackerHand></attackerHand>
            <defenderHand></defenderHand>
            <attackerField>
              <Card xsi:nil="true"/>
              <Card/>
            </attackerField>
            <defenderField>
              <Card xsi:nil="true"/>
            </defenderField>
          </Game>

        val attacker = mock[IPlayer]
        val defender = mock[IPlayer]
        val gameCards = mock[IGameCards]
        val scores = mock[IScores]
        val roles = mock[IRoles]

        when(mockPlayerDeserializer.fromXml(any[Elem])) thenReturn attacker thenReturn defender
        when(mockGameCardsFactory.createFromData(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(gameCards)
        when(mockRolesFactory.create(attacker, defender)).thenReturn(roles)
        when(mockScoresFactory.createWithScores(any())).thenReturn(scores)

        val result = deserializer.fromXml(xml)
        result shouldBe a[IGameState]
      }

      "throw exception for invalid card elements" in {
        val xml =
          <Game>
            <attacker><Player name="A" type="Human"/></attacker>
            <defender><Player name="B" type="Human"/></defender>
            <attackerHand><Card><invalid/></Card></attackerHand>
            <defenderHand></defenderHand>
          </Game>

        val attacker = mock[IPlayer]
        val defender = mock[IPlayer]

        when(mockPlayerDeserializer.fromXml(any[Elem])) thenReturn attacker thenReturn defender
        when(mockCardDeserializer.fromXml(any[Elem])).thenThrow(new IllegalArgumentException("Invalid card"))

        assertThrows[IllegalArgumentException] {
          deserializer.fromXml(xml)
        }
      }

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
    "gracefully handle missing optional JSON fields" in {
      val json: JsObject = Json.obj(
        "attacker" -> Json.obj("name" -> "Alice", "type" -> "Human"),
        "defender" -> Json.obj("name" -> "Bob", "type" -> "Human")
        // attackerHand, defenderHand, attackerField, etc. are omitted
      )

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val gameCards = mock[IGameCards]
      val scores = mock[IScores]
      val roles = mock[IRoles]

      when(mockPlayerDeserializer.fromJson(any[JsObject])) thenReturn attacker thenReturn defender
      when(mockGameCardsFactory.createFromData(
        eqTo(attacker), eqTo(Nil),
        eqTo(defender), eqTo(Nil),
        eqTo(List(None, None, None)),
        eqTo(List(None, None, None)),
        eqTo(None), eqTo(None))
      ).thenReturn(gameCards)

      when(mockRolesFactory.create(attacker, defender)).thenReturn(roles)
      when(mockScoresFactory.createWithScores(any())).thenReturn(scores)

      val result = deserializer.fromJson(json)

      result shouldBe a[IGameState]
    }
    "throw exception when defenderField contains invalid JSON" in {
      val json = Json.obj(
        "attacker" -> Json.obj("name" -> "Alice", "type" -> "Human"),
        "defender" -> Json.obj("name" -> "Bob", "type" -> "Human"),
        "defenderField" -> Json.arr("invalid-card")
      )

      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      when(mockPlayerDeserializer.fromJson(any[JsObject])) thenReturn attacker thenReturn defender

      val exception = intercept[IllegalArgumentException] {
        deserializer.fromJson(json)
      }

      exception.getMessage should include("Invalid card JSON")
    }

  }
}
