package de.htwg.se.soccercardclash.model.playerComponent.ai

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.AIPresetRegistry
import de.htwg.se.soccercardclash.model.playerComponent.ai.types.*
import de.htwg.se.soccercardclash.model.playerComponent.base.AI
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.CanPerformAction
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies.*
import de.htwg.se.soccercardclash.model.playerComponent.util.*
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class AIPresetRegistrySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "AIPresetRegistry" should {
    "register all core AI players with correct names and limits" in {
      val randoms: Map[String, IRandomProvider] = Map(
        "Taka" -> new RandomProvider(1),
        "Bitstorm" -> new RandomProvider(2),
        "Defendra" -> new RandomProvider(3),
        "MetaAI" -> new RandomProvider(4)
      )

      val result = AIPresetRegistry.registerCoreAIs(randoms)

      result should have size 4

      val taka = result("Taka")
      taka.name shouldBe "Taka"
      taka.playerType should matchPattern { case AI(_: TakaStrategy) => }
      taka.actionStates should contain allElementsOf Map(
        Boost -> CanPerformAction(2),
        DoubleAttack -> CanPerformAction(1),
        Swap -> CanPerformAction(1)
      )

      val bitstorm = result("Bitstorm")
      bitstorm.name shouldBe "Bitstorm"
      bitstorm.playerType should matchPattern { case AI(_: BitstormStrategy) => }
      bitstorm.actionStates should contain allElementsOf Map(
        Boost -> CanPerformAction(1),
        DoubleAttack -> CanPerformAction(5),
        Swap -> CanPerformAction(1)
      )

      val defendra = result("Defendra")
      defendra.name shouldBe "Defendra"
      defendra.playerType should matchPattern { case AI(_: DefendraStrategy) => }
      defendra.actionStates should contain allElementsOf Map(
        Boost -> CanPerformAction(7),
        DoubleAttack -> CanPerformAction(1),
        Swap -> CanPerformAction(3)
      )

      val metaAI = result("MetaAI")
      metaAI.name shouldBe "MetaAI"
      metaAI.playerType should matchPattern { case AI(_: MetaAIStrategy) => }
      metaAI.actionStates should contain allElementsOf Map(
        Boost -> CanPerformAction(3),
        DoubleAttack -> CanPerformAction(2),
        Swap -> CanPerformAction(3)
      )
    }
  }

}

