package de.htwg.se.soccercardclash.model.playerComponent.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer

class PlayerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A Player" should {

    "return the correct name" in {
      val player = Player("Alice")
      player.name shouldBe "Alice"
    }

    "update its name correctly" in {
      val player = Player("Bob")
      val renamed = player.setName("Robert")

      renamed.name shouldBe "Robert"
      renamed.getActionStates shouldBe player.getActionStates
    }

    "update action states correctly" in {
      val player = Player("Dana")
      val updated = player.updateActionState(PlayerActionPolicies.Boost, OutOfActions)

      updated.getActionStates(PlayerActionPolicies.Boost) shouldBe OutOfActions
    }

    "replace all action states when setActionStates is used" in {
      val player = Player("Eva")
      val newStates = Map(PlayerActionPolicies.Swap -> CanPerformAction(1))
      val updated = player.setActionStates(newStates)

      updated.getActionStates shouldBe newStates
    }

    "call performAction on the current state" in {
      val player = Player("Frank")
      val policy = PlayerActionPolicies.Boost
      val mockedState = mock[PlayerActionState]

      val stateMap = Map(policy -> mockedState)
      val playerWithMock = player.setActionStates(stateMap)

      val resultPlayer = mock[IPlayer]
      when(mockedState.performAction(playerWithMock, policy)).thenReturn(resultPlayer)

      playerWithMock.performAction(policy) shouldBe resultPlayer
    }

    "not change if an unknown action is performed" in {
      val player = Player("Grace", Map.empty)
      val unchanged = player.performAction(PlayerActionPolicies.Boost)

      unchanged shouldBe player
    }

    "have proper equality and hashCode based on name only" in {
      val p1 = Player("Hank")
      val p2 = Player("Hank", Map(PlayerActionPolicies.Swap -> OutOfActions))
      val p3 = Player("Ivy")

      p1 shouldEqual p2
      p1 should not equal p3
      p1.hashCode shouldBe p2.hashCode
    }

    "have correct toString format" in {
      val player = Player("Jack")
      player.toString shouldBe "Player: Jack"
    }

    "not be equal to a non-Player object" in {
      val player = Player("Leo")
      val notAPlayer = "Leo"

      player.equals(notAPlayer) shouldBe false
    }

    "not be equal to null" in {
      val player = Player("Mia")
      player.equals(null) shouldBe false
    }
  }
}
