package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.boostStrategy

import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.{BoostManager, IBoostManager, IBoostStrategy, IRevertStrategy, RevertBoostStrategy}
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class BoostManagerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  "BoostManager" should "apply a boost strategy and return true if successful" in {
    val mockField = mock[IPlayingField]
    val mockStrategy = mock[IBoostStrategy]

    when(mockStrategy.boost(mockField)).thenReturn(true)

    val manager = new BoostManager(mockField)
    val result = manager.applyBoost(mockStrategy)

    result shouldBe true
    verify(mockStrategy).boost(mockField)
  }

  it should "apply a boost strategy and return false if unsuccessful" in {
    val mockField = mock[IPlayingField]
    val mockStrategy = mock[IBoostStrategy]

    when(mockStrategy.boost(mockField)).thenReturn(false)

    val manager = new BoostManager(mockField)
    val result = manager.applyBoost(mockStrategy)

    result shouldBe false
    verify(mockStrategy).boost(mockField)
  }

  it should "provide a RevertBoostStrategy from getRevertStrategy" in {
    val mockField = mock[IPlayingField]

    val manager = new BoostManager(mockField)
    val strategy = manager.getRevertStrategy

    strategy shouldBe a [RevertBoostStrategy]
  }
}
