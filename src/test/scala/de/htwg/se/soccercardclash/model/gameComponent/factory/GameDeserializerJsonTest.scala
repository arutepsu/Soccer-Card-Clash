package de.htwg.se.soccercardclash.model.gameComponent.factory

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import play.api.libs.json._
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.factory.IMementoManagerFactory
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.IMementoManager
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.components.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.gameComponent.state.{IGameState, IGameStateFactory}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.PlayingFieldDeserializer
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import org.mockito.Mockito.{verify, atLeast}
import org.mockito.ArgumentMatchers.any


class GameDeserializerJsonTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "fromJson" should "parse valid JSON and return IGameState" in {
    val gameStateFactory = mock[IGameStateFactory]
    val playingFieldDeserializer = mock[PlayingFieldDeserializer]
    val playerDeserializer = mock[PlayerDeserializer]
    val handCardsQueueDeserializer = mock[HandCardsQueueDeserializer]
    val handCardsQueueFactory = mock[IHandCardsQueueFactory]
    val cardDeserializer = mock[CardDeserializer]

    val deserializer = new GameDeserializer(
      gameStateFactory,
      playingFieldDeserializer,
      playerDeserializer,
      handCardsQueueDeserializer,
      handCardsQueueFactory,
      cardDeserializer
    )

    val dummyPlayingField = mock[IPlayingField]
    val dummyPlayer1 = mock[IPlayer]
    val dummyPlayer2 = mock[IPlayer]
    val dummyCard = mock[ICard]
    val dummyHand1 = new HandCardsQueue(List(dummyCard))
    val dummyHand2 = new HandCardsQueue(List(dummyCard))
    val dummyState = mock[IGameState]

    val json: JsObject = Json.obj(
      "playingField" -> Json.obj(
        "attacker" -> Json.obj("dummy" -> "data"),
        "defender" -> Json.obj("dummy" -> "data"),
        "scores" -> Json.obj(
          "scorePlayer1" -> 5,
          "scorePlayer2" -> 4
        )
      ),
      "player1Hand" -> Json.arr(Json.obj("card" -> "c1")),
      "player2Hand" -> Json.arr(Json.obj("card" -> "c2")),
      "player1Field" -> Json.arr(Json.obj("card" -> "f1")),
      "player2Field" -> Json.arr(Json.obj("card" -> "f2")),
      "player1Goalkeeper" -> Json.obj("card" -> "gk1"),
      "player2Goalkeeper" -> Json.obj("card" -> "gk2")
    )

    when(playingFieldDeserializer.fromJson(any())).thenReturn(dummyPlayingField)
    when(playerDeserializer.fromJson(any())).thenReturn(dummyPlayer1, dummyPlayer2)
    when(cardDeserializer.fromJson(any())).thenReturn(dummyCard)
    when(handCardsQueueDeserializer.fromJson(any())).thenReturn(dummyHand1, dummyHand2)
    when(handCardsQueueFactory.create(Nil)).thenReturn(new HandCardsQueue(Nil)) // fallback case
    when(dummyPlayer1.getActionStates).thenReturn(Map.empty)
    when(dummyPlayer2.getActionStates).thenReturn(Map.empty)
    when(gameStateFactory.create(
      dummyPlayingField,
      dummyPlayer1,
      dummyPlayer2,
      dummyHand1,
      dummyHand2,
      List(dummyCard),
      List(dummyCard),
      Some(dummyCard),
      Some(dummyCard),
      5,
      4
    )).thenReturn(dummyState)

    val result = deserializer.fromJson(json)

    result shouldBe dummyState
    verify(playingFieldDeserializer).fromJson(any())
    verify(playerDeserializer, times(2)).fromJson(any())
    verify(cardDeserializer, org.mockito.Mockito.atLeast(4)).fromJson(any())
    verify(handCardsQueueDeserializer, times(2)).fromJson(any())
    verify(gameStateFactory).create(
      dummyPlayingField,
      dummyPlayer1,
      dummyPlayer2,
      dummyHand1,
      dummyHand2,
      List(dummyCard),
      List(dummyCard),
      Some(dummyCard),
      Some(dummyCard),
      5,
      4
    )
  }
}
