package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.action.manager.IPlayerActionManager
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{GameActionEvent, StateEvent}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

class GoalkeeperBoostStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "GoalkeeperBoostStrategy" should {

    "boost goalkeeper when allowed" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val roles = mock[IRoles]
      val goalkeeper = mock[ICard]
      val boostedGoalkeeper = mock[ICard]
      val updatedGameCards = mock[IGameCards]
      val updatedState = mock[IGameState]
      val finalState = mock[IGameState]
      val playerActionService = mock[IPlayerActionManager]

      when(state.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(attacker)
      when(roles.defender).thenReturn(defender)
      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerGoalkeeper(attacker)).thenReturn(Some(goalkeeper))
      when(playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)).thenReturn(true)
      when(goalkeeper.boost()).thenReturn(boostedGoalkeeper)
      when(gameCards.newGoalkeeperForAttacker(attacker, boostedGoalkeeper)).thenReturn(updatedGameCards)
      when(playerActionService.performAction(attacker, PlayerActionPolicies.Boost)).thenReturn(attacker)
      when(state.newGameCards(updatedGameCards)).thenReturn(updatedState)
      when(updatedState.newRoles(any())).thenReturn(finalState)

      val strategy = new GoalkeeperBoostStrategy(playerActionService)
      val (success, newState, events) = strategy.boost(state)

      success shouldBe true
      newState shouldBe finalState
      events should contain(GameActionEvent.BoostGoalkeeper)
    }

    "fail when no goalkeeper is present" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val roles = mock[IRoles]

      when(state.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(mock[IPlayer])
      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerGoalkeeper(any())).thenReturn(None)

      val playerActionService = mock[IPlayerActionManager]
      val strategy = new GoalkeeperBoostStrategy(playerActionService)

      val (success, newState, events) = strategy.boost(state)

      success shouldBe false
      newState shouldBe state
      events shouldBe empty
    }

    "fail when boost not allowed" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val roles = mock[IRoles]

      when(state.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(attacker)
      when(state.getGameCards).thenReturn(gameCards)
      when(gameCards.getPlayerGoalkeeper(attacker)).thenReturn(Some(mock[ICard]))

      val playerActionService = mock[IPlayerActionManager]
      when(playerActionService.canPerform(attacker, PlayerActionPolicies.Boost)).thenReturn(false)

      val strategy = new GoalkeeperBoostStrategy(playerActionService)

      val (success, newState, events) = strategy.boost(state)

      success shouldBe false
      newState shouldBe state
      events should contain(StateEvent.NoBoostsEvent(attacker))
    }
  }
}
