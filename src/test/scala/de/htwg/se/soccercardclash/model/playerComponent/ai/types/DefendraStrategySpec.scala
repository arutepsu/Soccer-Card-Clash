package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import de.htwg.se.soccercardclash.util.*
import org.mockito.ArgumentMatchers.anyInt

class DefendraStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "DefendraStrategy" should {
    "use only strategies the player is allowed to perform" in {
      val mockCtx = mock[GameContext]
      val mockPlayer = mock[IPlayer]
      val mockRandom = mock[IRandomProvider]

      val mockAttack = mock[IAIStrategy]
      val mockRandomAttack = mock[RandomAttackAIStrategy]
      val mockBoost = mock[SmartBoostWeakestDefenderAIStrategy]
      val mockSwap = mock[SimpleSwapAIStrategy]
      val mockAction = mock[AIAction]

      val strategy = new DefendraStrategy(mockRandom) {
        override protected val strategies: Vector[IAIStrategy] =
          Vector(mockAttack, mockRandomAttack, mockBoost, mockSwap)
      }

      val spyManager = spy(new PlayerActionManager)
      doReturn(false).when(spyManager).canPerform(mockPlayer, PlayerActionPolicies.Boost)
      doReturn(true).when(spyManager).canPerform(mockPlayer, PlayerActionPolicies.Swap)

      // Adjust eligible strategies manually
      val eligible = Vector(mockAttack, mockRandomAttack, mockSwap)
      val overweight = eligible.flatMap {
        case s: RandomAttackAIStrategy => Seq(s)
        case s => Seq(s, s)
      }

      // Simulate choice index
      when(mockRandom.nextInt(overweight.length)).thenReturn(1)
      when(overweight(1).decideAction(mockCtx, mockPlayer)).thenReturn(mockAction)
      when(mockPlayer.getActionStates).thenReturn(Map(
        PlayerActionPolicies.Swap -> CanPerformAction(1),
        PlayerActionPolicies.Boost -> OutOfActions
      ))

      val result = strategy.decideAction(mockCtx, mockPlayer)

      result shouldBe mockAction
      verify(mockRandom).nextInt(overweight.length)
      verify(overweight(1)).decideAction(mockCtx, mockPlayer)
    }

    "return NoOpAIAction if no strategies are eligible" in {
      val mockCtx = mock[GameContext]
      val mockPlayer = mock[IPlayer]
      val mockRandom = mock[IRandomProvider]

      val mockBoost = mock[SmartBoostWeakestDefenderAIStrategy]
      val mockSwap = mock[SimpleSwapAIStrategy]

      when(mockPlayer.getActionStates).thenReturn(
        Map(
          PlayerActionPolicies.Boost -> OutOfActions,
          PlayerActionPolicies.Swap -> OutOfActions
        )
      )

      when(mockRandom.nextInt(anyInt())).thenReturn(0)

      val strategy = new DefendraStrategy(mockRandom) {
        override protected val strategies: Vector[IAIStrategy] =
          Vector(mockBoost, mockSwap)
      }

      val result = strategy.decideAction(mockCtx, mockPlayer)

      result shouldBe NoOpAIAction
    }
  }
}
