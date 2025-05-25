package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.{NoOpAIAction, AIAction}
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import java.util.Random

class BitstormStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {
  class FixedRandom(indexToReturn: Int) extends Random {
    override def nextInt(bound: Int): Int = indexToReturn
  }

  "BitstormStrategy" should {

    "delegate decision to a randomly selected strategy" in {
      val mockCtx = mock[GameContext]
      val mockPlayer = mock[IPlayer]

      val mockAggressive = mock[IAIStrategy]
      val mockBoost = mock[IAIStrategy]
      val mockSwap = mock[IAIStrategy]

      val expectedAction = NoOpAIAction
      when(mockBoost.decideAction(any(), any())).thenReturn(expectedAction)

      val fakeStrategies = Vector(mockAggressive, mockBoost, mockSwap)
      val fixedRandom = new FixedRandom(1) // always returns index 1

      // Create testable version of BitstormStrategy with injected mocks
      val strategy = new BitstormStrategy(fixedRandom) {
        override val strategies: Vector[IAIStrategy] = fakeStrategies
      }

      val result = strategy.decideAction(mockCtx, mockPlayer)

      result shouldBe expectedAction
      verify(mockBoost).decideAction(mockCtx, mockPlayer)
    }
  }
}
