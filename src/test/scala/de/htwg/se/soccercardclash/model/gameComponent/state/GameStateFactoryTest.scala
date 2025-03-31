package de.htwg.se.soccercardclash.model.gameComponent.state

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import de.htwg.se.soccercardclash.model.gameComponent.state.memento.base.Memento
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.factory.CardDeserializer
import de.htwg.se.soccercardclash.model.gameComponent.IGame
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.PlayerDeserializer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.factory.PlayingFieldDeserializer
import de.htwg.se.soccercardclash.util.{Deserializer, Serializable}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.HandCardsQueue

class GameStateFactoryTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "GameStateFactory" should "create a GameState with correct values" in {
    val handCardsQueueFactory = mock[IHandCardsQueueFactory]
    val factory = new GameStateFactory(handCardsQueueFactory)

    val playingField = mock[IPlayingField]
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val card = mock[ICard]
    val player1Hand = new HandCardsQueue(List(card))
    val player2Hand = new HandCardsQueue(List(card))
    val player1Field = List(card)
    val player2Field = List(card)
    val player1GK = Some(card)
    val player2GK = Some(card)
    val player1Score = 3
    val player2Score = 4

    val actionStates1 = Map(
      PlayerActionPolicies.Boost -> CanPerformAction(2),
      PlayerActionPolicies.DoubleAttack -> OutOfActions
    )
    val actionStates2 = Map(
      PlayerActionPolicies.Swap -> CanPerformAction(1)
    )

    when(player1.getActionStates).thenReturn(actionStates1)
    when(player2.getActionStates).thenReturn(actionStates2)

    when(handCardsQueueFactory.create(player1Hand.toList)).thenReturn(player1Hand)
    when(handCardsQueueFactory.create(player2Hand.toList)).thenReturn(player2Hand)

    val gameState = factory.create(
      playingField,
      player1,
      player2,
      player1Hand,
      player2Hand,
      player1Field,
      player2Field,
      player1GK,
      player2GK,
      player1Score,
      player2Score
    )

    // ✅ Assertions using the public API
    gameState should not be null
    gameState.playingField shouldBe playingField
    gameState.player1 shouldBe player1
    gameState.player2 shouldBe player2
    gameState.player1Hand.toList shouldBe player1Hand.toList
    gameState.player2Hand.toList shouldBe player2Hand.toList
    gameState.player1Score shouldBe player1Score
    gameState.player2Score shouldBe player2Score
    gameState.player1Defenders shouldBe player1Field
    gameState.player2Defenders shouldBe player2Field
    gameState.player1Goalkeeper shouldBe player1GK
    gameState.player2Goalkeeper shouldBe player2GK
  }
}
