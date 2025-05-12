package de.htwg.se.soccercardclash.model.gameComponent.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import scala.xml.Elem
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
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.factory.PlayingFieldDeserializer
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}

class GameDeserializerXmlTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "fromXml" should "parse valid XML and return IGameState" in {
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

    val dummyPlayingField = mock[IGameState]
    val dummyPlayer1 = mock[IPlayer]
    val dummyPlayer2 = mock[IPlayer]
    val dummyCard = mock[ICard]
    val dummyHand1 = new HandCardsQueue(List(dummyCard))
    val dummyHand2 = new HandCardsQueue(List(dummyCard))
    val dummyState = mock[IGameState]

    // XML input
    val xml: Elem =
      <game>
        <playingField>
          <Attacker>
            <Player></Player>
          </Attacker>
          <Defender>
            <Player></Player>
          </Defender>
          <Scores>
            <PlayerScores>
              <ScorePlayer1>3</ScorePlayer1>
              <ScorePlayer2>2</ScorePlayer2>
            </PlayerScores>
          </Scores>
        </playingField>
        <player1Hand>
          <Card></Card>
        </player1Hand>
        <player2Hand>
          <Card></Card>
        </player2Hand>
        <player1Field>
          <Card></Card>
        </player1Field>
        <player2Field>
          <Card></Card>
        </player2Field>
        <player1Goalkeeper>
          <Card></Card>
        </player1Goalkeeper>
        <player2Goalkeeper>
          <Card></Card>
        </player2Goalkeeper>
      </game>

    when(playingFieldDeserializer.fromXml(any())).thenReturn(dummyPlayingField)
    when(playerDeserializer.fromXml(any())).thenReturn(dummyPlayer1, dummyPlayer2)
    when(cardDeserializer.fromXml(any())).thenReturn(dummyCard)
    when(handCardsQueueFactory.create(any[List[ICard]])).thenReturn(dummyHand1, dummyHand2)
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
      3,
      2
    )).thenReturn(dummyState)

    val result = deserializer.fromXml(xml)

    result shouldBe dummyState
    verify(playingFieldDeserializer).fromXml(any())
    verify(playerDeserializer, times(2)).fromXml(any())
    verify(cardDeserializer, atLeastOnce()).fromXml(any())
    verify(handCardsQueueFactory, times(2)).create(any[List[ICard]])
  }
}
