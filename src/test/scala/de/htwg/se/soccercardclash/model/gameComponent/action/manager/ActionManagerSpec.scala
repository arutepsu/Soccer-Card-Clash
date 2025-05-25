package de.htwg.se.soccercardclash.model.gameComponent.action.manager

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attackStrategy.IAttackManager
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boostStrategy.IBoostManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swapStrategy.ISwapManager
import de.htwg.se.soccercardclash.util.ObservableEvent
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{eq => meq, any}

class ActionManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "ActionManager" should {

    val mockService = mock[IPlayerActionManager]
    val mockAttackManager = mock[IAttackManager]
    val mockBoostManager = mock[IBoostManager]
    val mockSwapManager = mock[ISwapManager]
    val state = mock[IGameState]
    val events = List(mock[ObservableEvent])

    val manager = new ActionManager(mockService, mockAttackManager, mockBoostManager, mockSwapManager)

    "delegate singleAttack correctly" in {
      when(mockAttackManager.executeAttack(any(), any())).thenReturn((true, state, events))

      val (success, newState, returnedEvents) = manager.singleAttack(state, 0)

      success shouldBe true
      newState shouldBe state
      returnedEvents shouldBe events

      verify(mockAttackManager).executeAttack(any(), meq(state))
    }

    "delegate doubleAttack correctly" in {
      reset(mockAttackManager)
      when(mockAttackManager.executeAttack(any(), any())).thenReturn((false, state, events))

      val (success, _, returnedEvents) = manager.doubleAttack(state, 1)

      success shouldBe false
      returnedEvents shouldBe events

      verify(mockAttackManager).executeAttack(any(), meq(state))
    }

    "delegate reverseSwap correctly" in {

      when(mockSwapManager.swapAttacker(any(), any())).thenReturn((true, state, events))

      val (success, newState, returnedEvents) = manager.reverseSwap(state)

      success shouldBe true
      newState shouldBe state
      returnedEvents shouldBe events

      verify(mockSwapManager).swapAttacker(any(), meq(state))
    }

    "delegate regularSwap correctly" in {
      reset(mockSwapManager)

      when(mockSwapManager.swapAttacker(any(), any())).thenReturn((true, state, events))

      val (success, newState, returnedEvents) = manager.regularSwap(state, 2)

      success shouldBe true
      newState shouldBe state
      returnedEvents shouldBe events

      verify(mockSwapManager).swapAttacker(any(), meq(state))
    }

    "delegate boostDefender correctly" in {
      when(mockBoostManager.applyBoost(any(), any())).thenReturn((true, state, events))

      val (success, newState, returnedEvents) = manager.boostDefender(state, 3)

      success shouldBe true
      newState shouldBe state
      returnedEvents shouldBe events

      verify(mockBoostManager).applyBoost(any(), meq(state))
    }

    "delegate boostGoalkeeper correctly" in {
      reset(mockBoostManager)
      when(mockBoostManager.applyBoost(any(), any())).thenReturn((false, state, events))

      val (success, _, returnedEvents) = manager.boostGoalkeeper(state)

      success shouldBe false
      returnedEvents shouldBe events

      verify(mockBoostManager).applyBoost(any(), meq(state))
    }

    "return the injected action services" in {
      manager.getPlayerActionService shouldBe mockService
      manager.getBoostManager shouldBe mockBoostManager
    }
  }
}
