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

    "return the correct name and cards" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val player = Player("Alice", List(card1, card2))

      player.name shouldBe "Alice"
      player.getCards should contain inOrderOnly (card1, card2)
    }

    "update its name correctly" in {
      val player = Player("Bob", Nil)
      val renamed = player.setName("Robert")

      renamed.name shouldBe "Robert"
      renamed.cards shouldBe player.cards
    }

    "update its hand cards correctly" in {
      val card1 = mock[ICard]
      val card2 = mock[ICard]

      val player = Player("Charlie", List())
      val updated = player.setHandCards(List(card1, card2))

      updated.getCards should contain theSameElementsInOrderAs List(card1, card2)
    }

    "update action states correctly" in {
      val player = Player("Dana", Nil)
      val updated = player.updateActionState(PlayerActionPolicies.Boost, OutOfActions)

      updated.getActionStates(PlayerActionPolicies.Boost) shouldBe OutOfActions
    }

    "replace all action states when setActionStates is used" in {
      val player = Player("Eva", Nil)
      val newStates = Map(PlayerActionPolicies.Swap -> CanPerformAction(1))
      val updated = player.setActionStates(newStates)

      updated.getActionStates shouldBe newStates
    }

    "call performAction on the current state" in {
      val player = Player("Frank", Nil)
      val policy = PlayerActionPolicies.Boost
      val mockedState = mock[PlayerActionState]

      val stateMap = Map(policy -> mockedState)
      val playerWithMock = player.setActionStates(stateMap)

      val resultPlayer = mock[IPlayer]
      when(mockedState.performAction(playerWithMock, policy)).thenReturn(resultPlayer)

      playerWithMock.performAction(policy) shouldBe resultPlayer
    }

    "not change if an unknown action is performed" in {
      val player = Player("Grace", Nil, Map.empty)
      val unchanged = player.performAction(PlayerActionPolicies.Boost)

      unchanged shouldBe player
    }

    "have proper equality and hashCode based on name only" in {
      val p1 = Player("Hank", Nil)
      val p2 = Player("Hank", List(mock[ICard]))
      val p3 = Player("Ivy", Nil)

      p1 shouldEqual p2
      p1 should not equal p3
      p1.hashCode shouldBe p2.hashCode
    }

    "have correct toString format" in {
      val card = mock[ICard]
      when(card.toString).thenReturn("Ace_of_Spades")

      val player = Player("Jack", List(card))

      player.toString should include ("Player: Jack")
      player.toString should include ("Ace_of_Spades")
    }
    "not be equal to a non-Player object" in {
      val player = Player("Leo", Nil)
      val notAPlayer = "Leo"

      player.equals(notAPlayer) shouldBe false
    }
    "not be equal to null" in {
      val player = Player("Mia", Nil)
      player.equals(null) shouldBe false
    }
  }
}
