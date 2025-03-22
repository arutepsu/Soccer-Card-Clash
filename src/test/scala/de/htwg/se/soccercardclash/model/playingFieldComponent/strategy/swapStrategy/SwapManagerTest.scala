package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.swapStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.swapStrategy.{ISwapManager, ISwapStrategy, SwapManager}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class SwapManagerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "SwapManager" should "delegate to strategy and return true if successful" in {
    val mockField = mock[IPlayingField]
    val mockStrategy = mock[ISwapStrategy]

    when(mockStrategy.swap(mockField)).thenReturn(true)

    val manager = new SwapManager(mockField)
    val result = manager.swapAttacker(mockStrategy)

    result shouldBe true
    verify(mockStrategy).swap(mockField)
  }

  it should "return false if strategy returns false" in {
    val mockField = mock[IPlayingField]
    val mockStrategy = mock[ISwapStrategy]

    when(mockStrategy.swap(mockField)).thenReturn(false)

    val manager = new SwapManager(mockField)
    val result = manager.swapAttacker(mockStrategy)

    result shouldBe false
    verify(mockStrategy).swap(mockField)
  }
}
