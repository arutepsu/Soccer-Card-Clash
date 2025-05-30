package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.IRevertStrategy
import de.htwg.se.soccercardclash.util.ObservableEvent
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class BoostManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "BoostManager" should {

    "apply boost using provided boost strategy" in {
      val mockState = mock[IGameState]
      val mockBoostStrategy = mock[IBoostStrategy]
      val expectedResult = (true, mockState, List(mock[ObservableEvent]))

      when(mockBoostStrategy.boost(any())).thenReturn(expectedResult)

      val manager = new BoostManager()
      val result = manager.applyBoost(mockBoostStrategy, mockState)

      result shouldBe expectedResult
      verify(mockBoostStrategy).boost(mockState)
    }

    "return a RevertBoostStrategy when requested" in {
      val mockState = mock[IGameState]
      val manager = new BoostManager()

      val strategy = manager.getRevertStrategy(mockState)
      strategy should not be null
    }
  }
}
