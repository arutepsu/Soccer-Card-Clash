package de.htwg.se.soccercardclash.model.playerComponent.playerAction

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerActionInitializerSpec extends AnyWordSpec with Matchers {

  "PlayerActionInitializer" should {

    "initialize all policies with default CanPerformAction states" in {
      val result = PlayerActionInitializer.withDefaults()

      result.keys should contain allElementsOf PlayerActionPolicies.values

      PlayerActionPolicies.values.foreach { policy =>
        result(policy) shouldBe CanPerformAction(policy.maxUses)
      }
    }

    "initialize all policies with custom limits and fallback to default maxUses" in {
      val custom = Map(
        PlayerActionPolicies.DoubleAttack -> 2,
        PlayerActionPolicies.Boost -> 0
      )

      val result = PlayerActionInitializer.withCustomLimits(custom)

      result(PlayerActionPolicies.DoubleAttack) shouldBe CanPerformAction(2)
      result(PlayerActionPolicies.Boost) shouldBe OutOfActions

      // All other policies should use default maxUses
      PlayerActionPolicies.values
        .filterNot(p => custom.contains(p))
        .foreach { policy =>
          result(policy) shouldBe CanPerformAction(policy.maxUses)
        }
    }
  }
}
