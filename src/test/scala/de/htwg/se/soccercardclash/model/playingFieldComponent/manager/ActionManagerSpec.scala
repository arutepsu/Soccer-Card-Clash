package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import org.mockito.ArgumentMatchers.any
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{ActionManager, IActionManager, IPlayerActionManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.AttackHandler
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.attackStrategy.base.{DoubleAttackStrategy, SingleAttackStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.{BoostManager, IBoostManager}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.base.{DefenderBoostStrategy, GoalkeeperBoostStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.SwapManager
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.swapStrategy.base.{RegularSwapStrategy, ReverseSwapStrategy}
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any
class ActionManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ActionManager" should {

    "initialize and return the correct playing field" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val manager = new ActionManager(mockField, mockService)

      manager.getPlayingField shouldBe mockField
    }

    "reset all internal strategies correctly" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val manager = new ActionManager(mockField, mockService)

      val oldBoostManager = manager.getBoostManager

      manager.reset()

      val newBoostManager = manager.getBoostManager

      oldBoostManager should not be theSameInstanceAs(newBoostManager)
    }

    "delegate singleAttack to AttackHandler with correct strategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockHandler = mock[AttackHandler]
      val manager = new ActionManager(mockField, mockService)
      manager.attackHandler = mockHandler

      when(mockHandler.executeAttack(any[SingleAttackStrategy])).thenReturn(true)

      val result = manager.singleAttack(1)
      result shouldBe true

      verify(mockHandler).executeAttack(any[SingleAttackStrategy])
    }

    "delegate doubleAttack to AttackHandler with correct strategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockHandler = mock[AttackHandler]
      val manager = new ActionManager(mockField, mockService)
      manager.attackHandler = mockHandler

      when(mockHandler.executeAttack(any[DoubleAttackStrategy])).thenReturn(false)

      val result = manager.doubleAttack(2, mockService)
      result shouldBe false

      verify(mockHandler).executeAttack(any[DoubleAttackStrategy])
    }

    "delegate reverseSwap to SwapManager with ReverseSwapStrategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockSwap = mock[SwapManager]
      val manager = new ActionManager(mockField, mockService)
      manager.swapStrategy = mockSwap

      when(mockSwap.swapAttacker(any[ReverseSwapStrategy])).thenReturn(true)

      val result = manager.reverseSwap(mockService)
      result shouldBe true

      verify(mockSwap).swapAttacker(any[ReverseSwapStrategy])
    }

    "delegate regularSwap to SwapManager with HandSwapStrategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockSwap = mock[SwapManager]
      val manager = new ActionManager(mockField, mockService)
      manager.swapStrategy = mockSwap

      when(mockSwap.swapAttacker(any[RegularSwapStrategy])).thenReturn(false)

      val result = manager.regularSwap(3, mockService)
      result shouldBe false

      verify(mockSwap).swapAttacker(any[RegularSwapStrategy])
    }

    "delegate boostDefender to BoostManager with DefenderBoostStrategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockBoost = mock[BoostManager]
      val manager = new ActionManager(mockField, mockService)
      manager.boostStrategy = mockBoost

      when(mockBoost.applyBoost(any[DefenderBoostStrategy])).thenReturn(true)

      val result = manager.boostDefender(0, mockService)
      result shouldBe true

      verify(mockBoost).applyBoost(any[DefenderBoostStrategy])
    }

    "delegate boostGoalkeeper to BoostManager with GoalkeeperBoostStrategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val mockBoost = mock[BoostManager]
      val manager = new ActionManager(mockField, mockService)
      manager.boostStrategy = mockBoost

      when(mockBoost.applyBoost(any[GoalkeeperBoostStrategy])).thenReturn(false)

      val result = manager.boostGoalkeeper(mockService)
      result shouldBe false

      verify(mockBoost).applyBoost(any[GoalkeeperBoostStrategy])
    }

    "return the current boost strategy" in {
      val mockField = mock[IPlayingField]
      val mockService = mock[IPlayerActionManager]
      val manager = new ActionManager(mockField, mockService)

      val boost = manager.getBoostManager
      boost shouldBe a[IBoostManager]
    }
  }
}