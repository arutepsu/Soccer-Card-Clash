package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.PlayerActionManager
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

class PlayerActionManagerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  val manager = new PlayerActionManager

  "performAction" should "update the player's state if the action is allowed" in {
    val player = mock[IPlayer]
    val initialState = CanPerformAction(2)
    val expectedState = CanPerformAction(1)

    when(player.getActionStates).thenReturn(Map(PlayerActionPolicies.Boost -> initialState))
    when(player.updateActionState(PlayerActionPolicies.Boost, expectedState)).thenReturn(player)

    val result = manager.performAction(player, PlayerActionPolicies.Boost)

    result shouldBe player
    verify(player).updateActionState(PlayerActionPolicies.Boost, expectedState)
  }

  it should "set action state to OutOfActions when last use is consumed" in {
    val player = mock[IPlayer]
    val initialState = CanPerformAction(1)

    when(player.getActionStates).thenReturn(Map(PlayerActionPolicies.Swap -> initialState))
    when(player.updateActionState(PlayerActionPolicies.Swap, OutOfActions)).thenReturn(player)

    val result = manager.performAction(player, PlayerActionPolicies.Swap)

    result shouldBe player
    verify(player).updateActionState(PlayerActionPolicies.Swap, OutOfActions)
  }

  it should "return the same player if action state is missing" in {
    val player = mock[IPlayer]

    when(player.getActionStates).thenReturn(Map.empty)

    val result = manager.performAction(player, PlayerActionPolicies.DoubleAttack)

    result shouldBe player
  }

  "canPerform" should "return true when action has remaining uses" in {
    val player = mock[IPlayer]

    when(player.getActionStates).thenReturn(Map(PlayerActionPolicies.Boost -> CanPerformAction(1)))

    manager.canPerform(player, PlayerActionPolicies.Boost) shouldBe true
  }

  it should "return false when action has 0 remaining uses" in {
    val player = mock[IPlayer]

    when(player.getActionStates).thenReturn(Map(PlayerActionPolicies.Swap -> CanPerformAction(0)))

    manager.canPerform(player, PlayerActionPolicies.Swap) shouldBe false
  }

  it should "return false when action is not present" in {
    val player = mock[IPlayer]

    when(player.getActionStates).thenReturn(Map.empty)

    manager.canPerform(player, PlayerActionPolicies.DoubleAttack) shouldBe false
  }

  "resetAllActions" should "reset each policy to its max uses" in {
    val player = mock[IPlayer]
    val expectedMap = Map(
      PlayerActionPolicies.Boost -> CanPerformAction(2),
      PlayerActionPolicies.DoubleAttack -> CanPerformAction(1),
      PlayerActionPolicies.Swap -> CanPerformAction(1)
    )

    when(player.setActionStates(expectedMap)).thenReturn(player)

    val result = manager.resetAllActions(player)

    result shouldBe player
    verify(player).setActionStates(expectedMap)
  }
}
