package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.HandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, StateEvent}
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ReverseSwapStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ReverseSwapStrategy" should {

    "fail if attacker cannot perform swap and has OutOfActions" in {
      val mockState = mock[IGameState]
      val mockGameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val mockService = mock[IPlayerActionManager]

      when(mockState.getGameCards).thenReturn(mockGameCards)
      when(mockState.getRoles).thenReturn(Roles(attacker, defender))
      when(attacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> OutOfActions))
      when(mockService.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(false)

      val strategy = new ReverseSwapStrategy(mockService)
      val (success, newState, events) = strategy.swap(mockState)

      success shouldBe false
      newState shouldBe mockState
      events should contain only StateEvent.NoSwapsEvent(attacker)
    }

    "fail if hand has less than two cards" in {
      val mockState = mock[IGameState]
      val mockGameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val mockService = mock[IPlayerActionManager]

      when(mockState.getGameCards).thenReturn(mockGameCards)
      when(mockState.getRoles).thenReturn(Roles(attacker, defender))
      when(attacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
      when(mockService.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(true)

      val hand = HandCardsQueue(List(mock[ICard]))
      when(mockGameCards.getPlayerHand(attacker)).thenReturn(hand)

      val strategy = new ReverseSwapStrategy(mockService)
      val (success, newState, events) = strategy.swap(mockState)

      success shouldBe false
      newState shouldBe mockState
      events shouldBe empty
    }

    "perform reverse swap successfully when valid" in {
      val mockState = mock[IGameState]
      val mockGameCards = mock[IGameCards]
      val mockNewGameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val updatedAttacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val mockService = mock[IPlayerActionManager]
      val reversedHand = HandCardsQueue(List(mock[ICard], mock[ICard]))

      when(mockState.getGameCards).thenReturn(mockGameCards)
      when(mockState.getRoles).thenReturn(Roles(attacker, defender))
      when(attacker.actionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(1)))
      when(mockService.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(true)

      val originalCards = List(mock[ICard], mock[ICard])
      val hand = HandCardsQueue(originalCards)
      when(mockGameCards.getPlayerHand(attacker)).thenReturn(hand)

      // Expect a reversed hand
      when(mockGameCards.newPlayerHand(attacker, HandCardsQueue(originalCards.reverse))).thenReturn(mockNewGameCards)
      when(mockState.newGameCards(mockNewGameCards)).thenReturn(mockState)
      when(mockService.performAction(attacker, PlayerActionPolicies.Swap)).thenReturn(updatedAttacker)
      when(mockState.newRoles(Roles(updatedAttacker, defender))).thenReturn(mockState)

      val strategy = new ReverseSwapStrategy(mockService)
      val (success, newState, events) = strategy.swap(mockState)

      success shouldBe true
      newState shouldBe mockState
      events should contain only GameActionEvent.ReverseSwap
    }
  }
}
