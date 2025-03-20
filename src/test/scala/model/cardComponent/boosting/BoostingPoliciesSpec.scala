package model.cardComponent.boosting

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.cardComponent.base.components.Value._
import model.cardComponent.boosting.BoostingPolicies
import model.cardComponent.base.components.Value

class BoostingPoliciesSpec extends AnyWordSpec with Matchers {

  "BoostingPolicies" should {

    "return the correct boost amount for each value" in {
      BoostingPolicies.getBoostAmount(Two) shouldBe 6
      BoostingPolicies.getBoostAmount(Three) shouldBe 5
      BoostingPolicies.getBoostAmount(Four) shouldBe 5
      BoostingPolicies.getBoostAmount(Five) shouldBe 4
      BoostingPolicies.getBoostAmount(Six) shouldBe 4
      BoostingPolicies.getBoostAmount(Seven) shouldBe 3
      BoostingPolicies.getBoostAmount(Eight) shouldBe 3
      BoostingPolicies.getBoostAmount(Nine) shouldBe 2
      BoostingPolicies.getBoostAmount(Ten) shouldBe 2
      BoostingPolicies.getBoostAmount(Jack) shouldBe 1
      BoostingPolicies.getBoostAmount(Queen) shouldBe 1
      BoostingPolicies.getBoostAmount(King) shouldBe 1
      BoostingPolicies.getBoostAmount(Ace) shouldBe 0
    }

    "return 0 boost amount for an invalid value" in {
      val fakeValue = Value.allValues.find(_ == Ace).getOrElse(Ace)
      BoostingPolicies.getBoostAmount(fakeValue) shouldBe 0
    }

    "set maxAllowedValue correctly" in {
      BoostingPolicies.maxAllowedValue shouldBe 14
    }
  }
}
