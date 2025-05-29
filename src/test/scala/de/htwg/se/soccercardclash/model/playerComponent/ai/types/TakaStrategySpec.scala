package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.action.manager.PlayerActionManager
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{OutOfActions, PlayerActionPolicies, PlayerActionState}
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.util.{AIAction, NoOpAIAction}
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TakaStrategySpec extends AnyWordSpec with Matchers {

  "TakaStrategy" should {

    "return NoOpAIAction if no strategies are eligible" in {
      val ctx = mock(classOf[GameContext])
      val player = mock(classOf[IPlayer])
      val random = mock(classOf[IRandomProvider])

      // Construct real strategies instead of mocking
      val boostStrategy = new SmartBoostWeakestDefenderAIStrategy()
      val swapStrategy = new SimpleSwapAIStrategy(random)
      val attackStrategy = mock(classOf[SmartAttackAIStrategy])
      val expectedAction = mock(classOf[AIAction])

      // Simulate player with no available actions
      when(player.getActionStates).thenReturn(
        Map(
          PlayerActionPolicies.Boost -> OutOfActions,
          PlayerActionPolicies.Swap -> OutOfActions
        )
      )

      // Force the selection index
      when(random.nextInt(anyInt())).thenReturn(0)

      val strategy = new TakaStrategy(random) {
        override val strategies: Vector[IAIStrategy] =
          Vector(attackStrategy, boostStrategy, swapStrategy)
      }

      // The only eligible one should be `attackStrategy`
      when(attackStrategy.decideAction(ctx, player)).thenReturn(expectedAction)

      val result = strategy.decideAction(ctx, player)
      result shouldBe expectedAction
    }
  }
}
