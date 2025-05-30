package de.htwg.se.soccercardclash.model.gameComponent.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.gameComponent.components.*
class HandCardsFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A HandCardsFactory" should {
    "create an empty HandCards" in {
      val queueFactory = mock[IHandCardsQueueFactory]
      val emptyQueue = mock[IHandCardsQueue]
      val factory = new HandCardsFactory(queueFactory)

      when(queueFactory.create(List())).thenReturn(emptyQueue)
      when(emptyQueue.toList).thenReturn(Nil)

      val handCards = factory.empty
      val player = mock[IPlayer]
      when(player.name).thenReturn("test")

      handCards.getPlayerHand(player).toList shouldBe empty
    }


    "create HandCards for two players" in {
      val p1 = mock[IPlayer]
      val p2 = mock[IPlayer]
      val c1 = mock[ICard]
      val c2 = mock[ICard]
      val q1 = mock[IHandCardsQueue]
      val q2 = mock[IHandCardsQueue]
      val queueFactory = mock[IHandCardsQueueFactory]

      when(p1.name).thenReturn("p1")
      when(p2.name).thenReturn("p2")
      when(queueFactory.create(List(c1))).thenReturn(q1)
      when(queueFactory.create(List(c2))).thenReturn(q2)

      val factory = new HandCardsFactory(queueFactory)
      val result = factory.create(p1, List(c1), p2, List(c2))

      result.getPlayerHand(p1) shouldBe q1
      result.getPlayerHand(p2) shouldBe q2
    }
  }

  "HandCards" should {
    val player = mock[IPlayer]
    val queueFactory = mock[IHandCardsQueueFactory]
    val card = mock[ICard]
    val queue = mock[IHandCardsQueue]
    when(player.name).thenReturn("player")
    when(queue.toList).thenReturn(List(card))

    val handCards = HandCards(Map("player" -> queue), queueFactory)

    "return the attacking card" in {
      handCards.getAttackingCard(player) shouldBe card
    }

    "return the defender card" in {
      handCards.getDefenderCard(player) shouldBe card
    }

    "fallback to empty hand when player not found" in {
      val unknownPlayer = mock[IPlayer]
      when(unknownPlayer.name).thenReturn("unknown")
      val emptyQueue = mock[IHandCardsQueue]
      when(queueFactory.create(List())).thenReturn(emptyQueue)

      handCards.getPlayerHand(unknownPlayer) shouldBe emptyQueue
    }

    "update a player's hand correctly" in {
      val newQueue = mock[IHandCardsQueue]
      val updated = handCards.newPlayerHand(player, newQueue)
      updated.getPlayerHand(player) shouldBe newQueue
    }

    "initialize both players' hands correctly" in {
      val p1 = mock[IPlayer]
      val p2 = mock[IPlayer]
      val q1 = mock[IHandCardsQueue]
      val q2 = mock[IHandCardsQueue]
      val c1 = mock[ICard]
      val c2 = mock[ICard]

      when(p1.name).thenReturn("p1")
      when(p2.name).thenReturn("p2")
      when(queueFactory.create(List(c1))).thenReturn(q1)
      when(queueFactory.create(List(c2))).thenReturn(q2)

      val initialized = handCards.initializePlayerHands(p1, List(c1), p2, List(c2))

      initialized.getPlayerHand(p1) shouldBe q1
      initialized.getPlayerHand(p2) shouldBe q2
    }
  }
}
