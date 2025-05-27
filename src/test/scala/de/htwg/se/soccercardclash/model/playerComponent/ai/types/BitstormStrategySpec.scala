package de.htwg.se.soccercardclash.model.playerComponent.ai.types

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.*
import de.htwg.se.soccercardclash.model.playerComponent.util.IRandomProvider
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.util.*

class BitstormStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BitstormStrategy" should {
    "delegate to the strategy returned by random index" in {
      val mockContext = mock[GameContext]
      val mockPlayer = mock[IPlayer]
      val mockRandom = mock[IRandomProvider]

      val mockStrategy1 = mock[IAIStrategy]
      val mockStrategy2 = mock[IAIStrategy]
      val mockStrategy3 = mock[IAIStrategy]

      val mockAction = mock[AIAction]

      val strategy = new BitstormStrategy(mockRandom) {
        override protected val strategies: Vector[IAIStrategy] =
          Vector(mockStrategy1, mockStrategy2, mockStrategy3)
      }

      when(mockRandom.nextInt(3)).thenReturn(1)
      when(mockStrategy2.decideAction(mockContext, mockPlayer)).thenReturn(mockAction)

      val result = strategy.decideAction(mockContext, mockPlayer)
      result shouldBe mockAction

      verify(mockRandom).nextInt(3)
      verify(mockStrategy2).decideAction(mockContext, mockPlayer)
    }
  }
}
