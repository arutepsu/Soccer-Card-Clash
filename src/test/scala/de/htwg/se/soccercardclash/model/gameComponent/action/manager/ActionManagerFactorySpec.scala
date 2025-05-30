package de.htwg.se.soccercardclash.model.gameComponent.action.manager

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.attack.IAttackManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.IBoostManager
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.swap.ISwapManager
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class ActionManagerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "An ActionManagerFactory" should {
    "create a valid ActionManager" in {
      val mockPlayerActionService = mock[IPlayerActionManager]
      val mockAttackManager = mock[IAttackManager]
      val mockBoostManager = mock[IBoostManager]
      val mockSwapManager = mock[ISwapManager]

      val factory = new ActionManagerFactory(
        mockPlayerActionService,
        mockAttackManager,
        mockBoostManager,
        mockSwapManager
      )

      val result = factory.create()

      result shouldBe a[ActionExecutor]
    }
  }
}
