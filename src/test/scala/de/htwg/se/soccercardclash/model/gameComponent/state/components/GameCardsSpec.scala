package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.{eq => eqTo, any}

class GameCardsSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameCards" should {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val handCards = mock[IHandCards]
    val fieldCards = mock[IFieldCards]

    val refillStrategy = mock[IRefillStrategy]
    val handCardsQueue = mock[IHandCardsQueue]

    when(handCards.getPlayerHand(player1)).thenReturn(handCardsQueue)
    when(handCards.getPlayerHand(player2)).thenReturn(handCardsQueue)
    when(handCards.getAttackingCard(player1)).thenReturn(mock[ICard])
    when(handCards.getDefenderCard(player2)).thenReturn(mock[ICard])

    val card = mock[ICard]
    val updatedFieldCards = mock[IFieldCards]
    val updatedHandCards = mock[IHandCards]

    "delegate getPlayerHand and getAttackingCard to handCards" in {
      val gameCards = GameCards(handCards, fieldCards)
      gameCards.getPlayerHand(player1) shouldBe handCardsQueue
      gameCards.getAttackingCard(player1) shouldBe handCards.getAttackingCard(player1)
    }

    "delegate getPlayerGoalkeeper to fieldCards" in {
      val gameCards = GameCards(handCards, fieldCards)
      gameCards.getPlayerGoalkeeper(player1)
      verify(fieldCards).getPlayerGoalkeeper(player1)
    }

    "create new GameCards with updated goalkeeper" in {
      when(fieldCards.newPlayerGoalkeeper(player1, Some(card))).thenReturn(updatedFieldCards)
      when(updatedFieldCards.getPlayerGoalkeeper(player1)).thenReturn(Some(card))

      val gameCards = GameCards(handCards, fieldCards)
      val updated = gameCards.newPlayerGoalkeeper(player1, Some(card))

      updated.getPlayerGoalkeeper(player1) shouldBe Some(card)
    }


    "initialize fields via strategy" in {
      val updatedOnce = mock[IGameCards]
      val updatedTwice = mock[IGameCards]

      when(refillStrategy.refillField(any(), eqTo(player1), eqTo(handCardsQueue))).thenReturn(updatedOnce)
      when(refillStrategy.refillField(eqTo(updatedOnce), eqTo(player2), eqTo(handCardsQueue))).thenReturn(updatedTwice)

      val gameCards = GameCards(handCards, fieldCards, refillStrategy)
      val initialized = gameCards.initializeFields(player1, player2)

      initialized shouldBe updatedTwice
    }

    "replace refill strategy" in {
      val customStrategy = mock[IRefillStrategy]
      val updatedGameCards = mock[IGameCards]

      when(customStrategy.refillField(any(), any(), any())).thenReturn(updatedGameCards)

      val gameCards = GameCards(handCards, fieldCards).newRefillStrategy(customStrategy)
      val attacker = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]

      // act
      val result = gameCards.initializeFields(attacker, mock[IPlayer])

      // assert
      verify(customStrategy).refillField(gameCards, attacker, handCards.getPlayerHand(attacker))
    }

  }
}
