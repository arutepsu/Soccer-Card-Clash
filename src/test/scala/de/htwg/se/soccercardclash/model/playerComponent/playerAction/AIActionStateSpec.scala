package de.htwg.se.soccercardclash.model.playerComponent.playerAction

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class AIActionStateSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "CanPerformAction" should {

    "decrease remainingUses when performing an action" in {
      val player = mock[IPlayer]
      val updatedPlayer = mock[IPlayer]

      val state = CanPerformAction(2)

      when(player.updateActionState(PlayerActionPolicies.Boost, CanPerformAction(1)))
        .thenReturn(updatedPlayer)

      val result = state.performAction(player, PlayerActionPolicies.Boost)

      result shouldBe updatedPlayer
    }

    "transition to OutOfActions when no uses remain" in {
      val player = mock[IPlayer]
      val updatedPlayer = mock[IPlayer]

      val state = CanPerformAction(1)

      when(player.updateActionState(PlayerActionPolicies.Swap, OutOfActions))
        .thenReturn(updatedPlayer)

      val result = state.performAction(player, PlayerActionPolicies.Swap)

      result shouldBe updatedPlayer
    }
  }

  "OutOfActions" should {

    "return the same player without changes" in {
      val player = mock[IPlayer]
      OutOfActions.performAction(player, PlayerActionPolicies.DoubleAttack) shouldBe player
    }
  }

  "PlayerActionPolicies.fromString" should {
    "return correct enum instance" in {
      PlayerActionPolicies.fromString("Boost") should contain(PlayerActionPolicies.Boost)
      PlayerActionPolicies.fromString("DoubleAttack") should contain(PlayerActionPolicies.DoubleAttack)
      PlayerActionPolicies.fromString("Swap") should contain(PlayerActionPolicies.Swap)
    }

    "return None for invalid strings" in {
      PlayerActionPolicies.fromString("Fly") shouldBe None
    }
  }

  "PlayerActionState.fromString" should {
    "parse CanPerformAction correctly" in {
      PlayerActionState.fromString("CanPerformAction(2)") shouldBe CanPerformAction(2)
    }

    "parse OutOfActions correctly" in {
      PlayerActionState.fromString("OutOfActions") shouldBe OutOfActions
    }

    "throw exception for invalid format" in {
      intercept[IllegalArgumentException] {
        PlayerActionState.fromString("InvalidState")
      }.getMessage should include("Invalid action state")
    }
  }
}
