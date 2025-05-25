package de.htwg.se.soccercardclash.model.playerComponent.ai

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.AIPresetRegistry
import de.htwg.se.soccercardclash.model.playerComponent.factory.IPlayerFactory
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.{eq => eqTo, any}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class AIPresetRegistrySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "AIPresetRegistry" should {

    "register all core AI players with correct names and limits" in {
      val factory = mock[IPlayerFactory]

      val taka = mock[IPlayer]
      val bitstorm = mock[IPlayer]
      val defendra = mock[IPlayer]
      val metaAI = mock[IPlayer]

      // Stub factory responses
      when(factory.createAIPlayer(eqTo("Taka"), any(), eqTo(Map(Boost -> 2, DoubleAttack -> 1, Swap -> 1)))).thenReturn(taka)
      when(factory.createAIPlayer(eqTo("Bitstorm"), any(), eqTo(Map(Boost -> 1, DoubleAttack -> 5, Swap -> 1)))).thenReturn(bitstorm)
      when(factory.createAIPlayer(eqTo("Defendra"), any(), eqTo(Map(Boost -> 7, DoubleAttack -> 1, Swap -> 3)))).thenReturn(defendra)
      when(factory.createAIPlayer(eqTo("MetaAI"), any(), eqTo(Map(Boost -> 3, DoubleAttack -> 2, Swap -> 3)))).thenReturn(metaAI)

      val result = AIPresetRegistry.registerCoreAIs(factory)

      result should have size 4
      result("Taka") shouldBe taka
      result("Bitstorm") shouldBe bitstorm
      result("Defendra") shouldBe defendra
      result("MetaAI") shouldBe metaAI
    }
  }
}
