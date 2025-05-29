package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{GameActionEvent, StateEvent}
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{any, eq as eqTo}

class DefenderBoostStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "DefenderBoostStrategy" should {

    "boost a defender card when allowed" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val roles = mock[IRoles]
      val card = mock[ICard]
      val boostedCard = mock[ICard]
      val updatedGameCards = mock[IGameCards]
      val updatedState = mock[IGameState]
      val updatedRoles = mock[IGameState]
      val playerActionService = mock[IPlayerActionManager]

      when(roles.attacker).thenReturn(attacker)
      when(roles.defender).thenReturn(defender)
      when(state.getRoles).thenReturn(roles)

      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerDefenders(attacker)).thenReturn(List(Some(card)))
      when(playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)).thenReturn(true)
      when(card.boost()).thenReturn(boostedCard)
      when(gameCards.newPlayerDefenders(attacker, List(Some(boostedCard)))).thenReturn(updatedGameCards)
      when(playerActionService.performAction(attacker, PlayerActionPolicies.Boost)).thenReturn(attacker)
      when(state.newGameCards(updatedGameCards)).thenReturn(updatedState)
      when(updatedState.newRoles(any())).thenReturn(updatedRoles)

      val strategy = new DefenderBoostStrategy(0, playerActionService)
      val (success, newState, events) = strategy.boost(state)

      success shouldBe true
      newState shouldBe updatedRoles
      events should contain(GameActionEvent.BoostDefender)
    }

    "not boost if index is invalid" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val roles = mock[IRoles]
      val playerActionService = mock[IPlayerActionManager]

      when(roles.attacker).thenReturn(attacker)
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerDefenders(attacker)).thenReturn(List.empty)

      val strategy = new DefenderBoostStrategy(5, playerActionService)
      val (success, newState, events) = strategy.boost(state)

      success shouldBe false
      newState shouldBe state
      events shouldBe empty
    }

    "not boost if card is missing or action not allowed" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val roles = mock[IRoles]
      val playerActionService = mock[IPlayerActionManager]

      when(roles.attacker).thenReturn(attacker)
      when(state.getRoles).thenReturn(roles)
      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerDefenders(attacker)).thenReturn(List(None))
      when(playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)).thenReturn(false)

      val strategy = new DefenderBoostStrategy(0, playerActionService)
      val (success, newState, events) = strategy.boost(state)

      success shouldBe false
      newState shouldBe state
      events should contain(StateEvent.NoBoostsEvent(attacker))
    }
  }
}
