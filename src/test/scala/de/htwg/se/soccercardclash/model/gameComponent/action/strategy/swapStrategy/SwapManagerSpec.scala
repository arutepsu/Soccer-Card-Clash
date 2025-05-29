package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.ISwapStrategy
import de.htwg.se.soccercardclash.util.ObservableEvent
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class SwapManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SwapManager" should {

    "delegate swap to provided swap strategy" in {
      val mockState = mock[IGameState]
      val mockSwapStrategy = mock[ISwapStrategy]
      val expectedResult = (true, mockState, List(mock[ObservableEvent]))

      when(mockSwapStrategy.swap(any())).thenReturn(expectedResult)

      val manager = new SwapManager()
      val result = manager.swapAttacker(mockSwapStrategy, mockState)

      result shouldBe expectedResult
      verify(mockSwapStrategy).swap(mockState)
    }
  }
}
