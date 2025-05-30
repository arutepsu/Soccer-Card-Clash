package de.htwg.se.soccercardclash.model.gameComponent.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.components.GameCards
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.*
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base.StandardRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.components.*
class GameCardsSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GameCards" should {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val handCards = mock[IHandCards]
    val fieldCards = mock[IFieldCards]

    val refillStrategy = mock[IRefillStrategy]
    val handCardsQueue = mock[IHandCardsQueue]
    val mockRefillStrategy = mock[IRefillStrategy]
    when(handCards.getPlayerHand(player1)).thenReturn(handCardsQueue)
    when(handCards.getPlayerHand(player2)).thenReturn(handCardsQueue)
    when(handCards.getAttackingCard(player1)).thenReturn(mock[ICard])
    when(handCards.getDefenderCard(player2)).thenReturn(mock[ICard])

    val card = mock[ICard]
    val updatedFieldCards = mock[IFieldCards]
    val updatedHandCards = mock[IHandCards]

    "delegate getPlayerHand and getAttackingCard to handCards" in {
      val gameCards = GameCards(handCards, fieldCards, mockRefillStrategy)
      gameCards.getPlayerHand(player1) shouldBe handCardsQueue
      gameCards.getAttackingCard(player1) shouldBe handCards.getAttackingCard(player1)
    }

    "delegate getPlayerGoalkeeper to fieldCards" in {
      val gameCards = GameCards(handCards, fieldCards, refillStrategy)
      gameCards.getPlayerGoalkeeper(player1)
      verify(fieldCards).getPlayerGoalkeeper(player1)
    }
    "return a copy with the new strategy" in {
      val mockHandCards = mock[IHandCards]
      val mockFieldCards = mock[IFieldCards]
      val oldStrategy = mock[IRefillStrategy]
      val newStrategy = mock[IRefillStrategy]

      val original = GameCards(mockHandCards, mockFieldCards, oldStrategy)

      val updated = original.newRefillStrategy(newStrategy)

      updated should not be theSameInstanceAs(original)
      updated shouldBe a[GameCards]

      updated.asInstanceOf[GameCards].refillStrategy shouldBe newStrategy
      updated.asInstanceOf[GameCards].handCards shouldBe mockHandCards
      updated.asInstanceOf[GameCards].fieldCards shouldBe mockFieldCards
    }
    "create new GameCards with updated goalkeeper" in {
      when(fieldCards.newPlayerGoalkeeper(player1, Some(card))).thenReturn(updatedFieldCards)
      when(updatedFieldCards.getPlayerGoalkeeper(player1)).thenReturn(Some(card))

      val gameCards = GameCards(handCards, fieldCards, refillStrategy)
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

      val gameCards = GameCards(handCards, fieldCards, customStrategy)
      val attacker = mock[IPlayer]
      val attackerHand = mock[IHandCardsQueue]

      val result = gameCards.initializeFields(attacker, mock[IPlayer])

      verify(customStrategy).refillField(gameCards, attacker, handCards.getPlayerHand(attacker))
    }

  }
  "GameCards" should {

    val player = mock[IPlayer]
    val otherPlayer = mock[IPlayer]

    val mockHandCards = mock[IHandCards]
    val mockFieldCards = mock[IFieldCards]
    val mockRefillStrategy = mock[IRefillStrategy]
    val newHand = mock[IHandCardsQueue]
    val defenderCard = mock[ICard]
    val goalkeeper = mock[ICard]

    "replace the hand for a player with newPlayerHand" in {
      val updatedHandCards = mock[IHandCards]
      when(mockHandCards.newPlayerHand(player, newHand)).thenReturn(updatedHandCards)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val updated = gameCards.newPlayerHand(player, newHand)

      updated.getPlayerHand(player) shouldBe updatedHandCards.getPlayerHand(player)
    }

    "get defender card at specific index" in {
      when(mockFieldCards.getDefenderCard(player, 1)).thenReturn(Some(defenderCard))

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      gameCards.getDefenderCard(player, 1) shouldBe Some(defenderCard)
    }

    "set new defenders for a player" in {
      val updatedField = mock[IFieldCards]
      val defenders = List(Some(defenderCard))

      when(mockFieldCards.newPlayerDefenders(player, defenders)).thenReturn(updatedField)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val updated = gameCards.newPlayerDefenders(player, defenders)

      updated.getPlayerDefenders(player) shouldBe updatedField.getPlayerDefenders(player)
    }

    "remove a specific defender card" in {
      val updatedField = mock[IFieldCards]
      when(mockFieldCards.removeDefenderCard(player, Some(defenderCard))).thenReturn(updatedField)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val updated = gameCards.removeDefenderCard(player, Some(defenderCard))

      updated.getPlayerDefenders(player) shouldBe updatedField.getPlayerDefenders(player)
    }

    "remove a goalkeeper for defender" in {
      val updatedField = mock[IFieldCards]
      when(mockFieldCards.removeDefenderGoalkeeper(player)).thenReturn(updatedField)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val updated = gameCards.removeDefenderGoalkeeper(player)

      updated.getPlayerGoalkeeper(player) shouldBe updatedField.getPlayerGoalkeeper(player)
    }

    "check if all defenders are beaten" in {
      when(mockFieldCards.allDefendersBeaten(player)).thenReturn(true)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      gameCards.allDefendersBeaten(player) shouldBe true
    }

    "get defender card at index" in {
      when(mockFieldCards.getDefenderCard(player, 0)).thenReturn(Some(defenderCard))

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      gameCards.getDefenderCardAt(player, 0) shouldBe Some(defenderCard)
    }

    "refill defender field" in {
      val updatedGameCards = mock[IGameCards]
      when(mockRefillStrategy.refillDefenderField(any[IGameCards], eqTo(player))).thenReturn(updatedGameCards)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val result = gameCards.refillDefenderField(player)

      result shouldBe updatedGameCards
    }

    "assign a new goalkeeper for attacker" in {
      val updatedField = mock[IFieldCards]
      when(mockFieldCards.newGoalkeeperForAttacker(player, goalkeeper)).thenReturn(updatedField)

      val gameCards = GameCards(mockHandCards, mockFieldCards, mockRefillStrategy)
      val updated = gameCards.newGoalkeeperForAttacker(player, goalkeeper)

      updated.getPlayerGoalkeeper(player) shouldBe updatedField.getPlayerGoalkeeper(player)
    }
  }
}
