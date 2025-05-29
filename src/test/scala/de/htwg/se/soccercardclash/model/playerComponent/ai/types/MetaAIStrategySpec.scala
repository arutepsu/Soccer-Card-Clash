package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyInt
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class MetaAIStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "MetaAIStrategy" should {

    "choose an eligible strategy and delegate to it" in {
      val ctx = mock[GameContext]
      val player = mock[IPlayer]
      val random = mock[IRandomProvider]
      val mockStrategy = mock[IAIStrategy]
      val expectedAction = mock[AIAction]

      when(random.nextInt(anyInt())).thenReturn(0)
      when(mockStrategy.decideAction(ctx, player)).thenReturn(expectedAction)

      val strategy = new MetaAIStrategy(random) {
        override val strategies: Vector[IAIStrategy] = Vector(mockStrategy)
      }

      val result = strategy.decideAction(ctx, player)
      result shouldBe expectedAction
    }

    "return NoOpAIAction if no strategies are eligible" in {
      val ctx = mock[GameContext]
      val player = mock[IPlayer]
      val random = mock[IRandomProvider]

      // Set up mock action states to prevent NPE
      when(player.getActionStates).thenReturn(
        Map(
          PlayerActionPolicies.Boost -> OutOfActions,
          PlayerActionPolicies.Swap -> OutOfActions
        )
      )

      val boostStrategy = mock[SmartBoostWeakestDefenderAIStrategy]
      val swapStrategy = mock[SimpleSwapAIStrategy]

      val strategy = new MetaAIStrategy(random) {
        override val strategies: Vector[IAIStrategy] = Vector(boostStrategy, swapStrategy)
      }

      val result = strategy.decideAction(ctx, player)

      result shouldBe NoOpAIAction
    }
  }
}
