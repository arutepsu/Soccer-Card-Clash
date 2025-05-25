package de.htwg.se.soccercardclash.model.playerComponent.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.context.GameContext
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.SimpleAttackAIStrategy
import de.htwg.se.soccercardclash.model.playerComponent.base.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.soccercardclash.model.playerComponent.base.Player
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.{CanPerformAction, OutOfActions, PlayerActionPolicies, PlayerActionState}
import de.htwg.se.soccercardclash.model.playerComponent.base.{AI, Human}
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.playerComponent.ai.strategies.IAIStrategy
import de.htwg.se.soccercardclash.util.NoOpAIAction
import org.mockito.ArgumentMatchers.any
class PlayerSpec extends AnyWordSpec with Matchers with MockitoSugar {


  val defaultName = "Alice"
  val humanPlayer = Player.withDefaultActions(defaultName, Human)
  val mockStrategy = mock[IAIStrategy]
  when(mockStrategy.decideAction(any(), any())).thenReturn(NoOpAIAction)

  val aiPlayer = Player.withDefaultActions("AI_Player", AI(mockStrategy))


  "A Player" should {

    "have the correct name and string representation" in {
      humanPlayer.name shouldBe defaultName
      humanPlayer.toString shouldBe "Player: Alice"
    }

    "return a new instance with updated name" in {
      val updated = humanPlayer.setName("Bob")
      updated.name shouldBe "Bob"
      updated should not be theSameInstanceAs(humanPlayer)
    }

    "update a single action state correctly" in {
      val action = PlayerActionPolicies.Swap
      val updated = humanPlayer.updateActionState(action, OutOfActions)
      updated.getActionStates(action) shouldBe OutOfActions
      updated should not be theSameInstanceAs(humanPlayer)
    }

    "set all action states with a new map" in {
      val newStates = PlayerActionPolicies.values.map(_ -> OutOfActions).toMap
      val updated = humanPlayer.setActionStates(newStates)
      updated.getActionStates.values.foreach(_ shouldBe OutOfActions)
    }

    "be equal to another player with the same name" in {
      val clone = Player.withDefaultActions(defaultName, Human)
      humanPlayer shouldEqual clone
    }

    "not be equal to another player with a different name" in {
      val other = Player.withDefaultActions("Charlie", Human)
      humanPlayer should not equal other
    }

    "have consistent hashCode based on name only" in {
      val mockStrategy = mock[IAIStrategy]
      val sameNameOther = Player.withDefaultActions(defaultName, AI(mockStrategy))

      humanPlayer.hashCode shouldBe sameNameOther.hashCode
    }


    "correctly identify AI players" in {
      aiPlayer.isAI shouldBe true
      humanPlayer.isAI shouldBe false
    }

    "delegate decision to strategy when AI" in {
      val ctx = mock[GameContext]

      aiPlayer.decideAction(ctx) shouldBe Some(NoOpAIAction)

      verify(mockStrategy).decideAction(ctx, aiPlayer)
    }


    "return None for decideAction if player is not AI" in {
      val ctx = mock[GameContext]
      humanPlayer.decideAction(ctx) shouldBe None
    }

    "construct with custom action limits properly" in {
      val limits = Map(
        PlayerActionPolicies.Swap -> 0,
        PlayerActionPolicies.DoubleAttack -> 2
      )

      val custom = Player.withCustomActions("Custom", Human, limits)
      custom.getActionStates(PlayerActionPolicies.Swap) shouldBe OutOfActions
      custom.getActionStates(PlayerActionPolicies.DoubleAttack) shouldBe CanPerformAction(2)
    }
  }
}
