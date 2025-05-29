package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.{IPlayer, playerAction}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.{GameActionEvent, ObservableEvent, StateEvent}

import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.{Failure, Success}

class RegularSwapStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RegularSwapStrategy" should {

    "perform a regular swap successfully" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val updatedHand = mock[IHandCardsQueue]
      val cards = mock[IGameCards]
      val state = mock[IGameState]
      val updatedState = mock[IGameState]
      val actionManager = mock[IPlayerActionManager]
      val updatedAttacker = mock[IPlayer]

      when(state.getRoles).thenReturn(Roles(attacker, defender))
      when(state.getGameCards).thenReturn(cards)
      when(cards.getPlayerHand(attacker)).thenReturn(hand)
      when(hand.getHandSize).thenReturn(3)
      when(hand.swap(0, 2)).thenReturn(Success(updatedHand))
      when(cards.newPlayerHand(attacker, updatedHand)).thenReturn(cards)
      when(actionManager.performAction(attacker, PlayerActionPolicies.Swap)).thenReturn(updatedAttacker)
      when(state.newGameCards(cards)).thenReturn(state)
      when(state.newRoles(Roles(updatedAttacker, defender))).thenReturn(updatedState)
      when(actionManager.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(true)

      val strategy = new RegularSwapStrategy(0, actionManager)
      val (success, result, events) = strategy.swap(state)

      success shouldBe true
      result shouldBe updatedState
      events should contain(GameActionEvent.RegularSwap)
    }

    "fail if swap index is invalid" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val state = mock[IGameState]
      val cards = mock[IGameCards]
      val actionManager = mock[IPlayerActionManager]

      when(state.getRoles).thenReturn(Roles(attacker, defender))
      when(state.getGameCards).thenReturn(cards)
      when(cards.getPlayerHand(attacker)).thenReturn(hand)
      when(hand.getHandSize).thenReturn(1)
      when(actionManager.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(true)

      val strategy = new RegularSwapStrategy(1, actionManager)
      val (success, _, events) = strategy.swap(state)

      success shouldBe false
      events shouldBe empty
    }

    "fail and return NoSwapsEvent if out of actions" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val state = mock[IGameState]
      val cards = mock[IGameCards]
      val actionManager = mock[IPlayerActionManager]

      when(state.getRoles).thenReturn(Roles(attacker, defender))
      when(state.getGameCards).thenReturn(cards)
      when(cards.getPlayerHand(attacker)).thenReturn(hand)
      when(actionManager.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(false)

      val strategy = new RegularSwapStrategy(0, actionManager)
      val (success, _, events) = strategy.swap(state)

      success shouldBe false
      events should contain(StateEvent.NoSwapsEvent(attacker))
    }

    "fail if swap attempt throws Failure" in {
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val state = mock[IGameState]
      val cards = mock[IGameCards]
      val actionManager = mock[IPlayerActionManager]

      when(state.getRoles).thenReturn(Roles(attacker, defender))
      when(state.getGameCards).thenReturn(cards)
      when(cards.getPlayerHand(attacker)).thenReturn(hand)
      when(hand.getHandSize).thenReturn(3)
      when(hand.swap(0, 2)).thenReturn(Failure(new Exception("Boom")))
      when(actionManager.canPerform(attacker, PlayerActionPolicies.Swap)).thenReturn(true)

      val strategy = new RegularSwapStrategy(0, actionManager)
      val (success, _, events) = strategy.swap(state)

      success shouldBe false
      events shouldBe empty
    }
  }
}
