package de.htwg.se.soccercardclash.model.playerComponent.factory

import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class PlayerFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "PlayerFactory" should {

    "create a Human Player with the given name" in {
      val name = "TestPlayer"
      val factory = new PlayerFactory()

      val player: IPlayer = factory.createPlayer(name)

      player.name shouldBe name
      player.isAI shouldBe false
    }

    "create an AI Player with default actions" in {
      val name = "Bot"
      val strategy = mock[IAIStrategy]
      val factory = new PlayerFactory()

      val aiPlayer = factory.createAIPlayer(name, strategy)

      aiPlayer.name shouldBe name
      aiPlayer.isAI shouldBe true
    }

    "create an AI Player with custom action limits" in {
      val name = "Bot"
      val strategy = mock[IAIStrategy]
      val limits = Map(
        PlayerActionPolicies.Boost -> 0,
        PlayerActionPolicies.DoubleAttack -> 2
      )
      val factory = new PlayerFactory()

      val aiPlayer = factory.createAIPlayer(name, strategy, limits)

      aiPlayer.name shouldBe name
      aiPlayer.isAI shouldBe true
      aiPlayer.getActionStates(PlayerActionPolicies.Boost) shouldBe OutOfActions
      aiPlayer.getActionStates(PlayerActionPolicies.DoubleAttack) shouldBe CanPerformAction(2)
    }
  }
}
