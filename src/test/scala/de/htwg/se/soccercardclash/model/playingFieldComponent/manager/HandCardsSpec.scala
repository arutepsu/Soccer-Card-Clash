package de.htwg.se.soccercardclash.model.playingFieldComponent.manager

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.HandCards
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.mutable
import scala.util.Success

class HandCardsSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A PlayerHandManager" should {

    "initialize player hands correctly" in {
      val factory = mock[IHandCardsQueueFactory]
      val manager = new HandCards(factory)

      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      when(player1.name).thenReturn("Alice")
      when(player2.name).thenReturn("Bob")

      val cards1 = List(mock[ICard], mock[ICard])
      val cards2 = List(mock[ICard])
      val hand1 = mock[IHandCardsQueue]
      val hand2 = mock[IHandCardsQueue]

      when(factory.create(cards1)).thenReturn(hand1)
      when(factory.create(cards2)).thenReturn(hand2)

      manager.initializePlayerHands(player1, cards1, player2, cards2)

      manager.getPlayerHand(player1) shouldBe hand1
      manager.getPlayerHand(player2) shouldBe hand2
    }

    "return a new hand if player hand is not initialized" in {
      val factory = mock[IHandCardsQueueFactory]
      val manager = new HandCards(factory)

      val player = mock[IPlayer]
      val defaultHand = mock[IHandCardsQueue]

      when(factory.create(List())).thenReturn(defaultHand)

      val result = manager.getPlayerHand(player)
      result shouldBe defaultHand
    }

    "set a new hand for a player" in {
      val factory = mock[IHandCardsQueueFactory]
      val manager = new HandCards(factory)

      val player = mock[IPlayer]
      val newHand = mock[IHandCardsQueue]

      manager.newPlayerHand(player, newHand)
      manager.getPlayerHand(player) shouldBe newHand
    }

    "return the last card for attacker and defender" in {
      val factory = mock[IHandCardsQueueFactory]
      val manager = new HandCards(factory)

      val player = mock[IPlayer]
      val card = mock[ICard]
      val queue = mock[IHandCardsQueue]

      // Fix: Return a List, not a Queue
      when(queue.toList).thenReturn(List(card))
      when(queue.getHandSize).thenReturn(1)
      when(queue.removeLastCard()).thenReturn(Success((card, queue)))

      manager.newPlayerHand(player, queue)

      manager.getAttackingCard(player) shouldBe card
      manager.getDefenderCard(player) shouldBe card
    }

    "clear all player hands" in {
      val factory = mock[IHandCardsQueueFactory]
      val manager = new HandCards(factory)

      val player = mock[IPlayer]
      val queue = mock[IHandCardsQueue]
      manager.newPlayerHand(player, queue)

      manager.clearAll()
      val newHand = mock[IHandCardsQueue]
      when(factory.create(List())).thenReturn(newHand)

      manager.getPlayerHand(player) shouldBe newHand
    }
  }
}
