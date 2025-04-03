package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.HandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base.RefillField
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class RefillFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RefillField" should {

    "fully refill field with 4 cards when empty" in {
      val fieldState = mock[IDataManager]
      val player = mock[IPlayer]

      val cards = (1 to 4).map { i =>
        val card = mock[ICard]
        when(card.valueToInt).thenReturn(i)
        card
      }.toList

      val hand = new HandCardsQueue(cards)

      when(fieldState.getPlayerDefenders(player)).thenReturn(List.empty)
      when(fieldState.getPlayerGoalkeeper(player)).thenReturn(None)

      val strategy = new RefillField
      val updatedHand = strategy.refill(fieldState, player, hand)

      val expectedGoalkeeper = cards.maxBy(_.valueToInt)
      val expectedDefenders = cards.filterNot(_ == expectedGoalkeeper)

      updatedHand.getHandSize shouldBe 0
      verify(fieldState).setPlayerField(player, cards)
      verify(fieldState).setPlayerGoalkeeper(player, Some(expectedGoalkeeper))
      verify(fieldState).setPlayerDefenders(player, expectedDefenders)
    }


    "partially refill with 2 cards if 1 defender is needed (field empty)" in {
      val fieldState = mock[IDataManager]
      val player = mock[IPlayer]

      val card1 = mock[ICard]; when(card1.valueToInt).thenReturn(5)
      val card2 = mock[ICard]; when(card2.valueToInt).thenReturn(8)
      val hand = new HandCardsQueue(List(card1, card2))

      // Simulate: 1 defender needed (e.g., already 1 on field, but getPlayerDefenders is empty to allow state update)
      when(fieldState.getPlayerDefenders(player)).thenReturn(List.empty)
      when(fieldState.getPlayerGoalkeeper(player)).thenReturn(None)

      val strategy = new RefillField
      strategy.refill(fieldState, player, hand)

      val expectedGoalkeeper = Some(card2)
      val expectedDefenders = List(card1)

      verify(fieldState).setPlayerField(player, List(card1, card2))
      verify(fieldState).setPlayerGoalkeeper(player, expectedGoalkeeper)
      verify(fieldState).setPlayerDefenders(player, expectedDefenders)
    }


    "do nothing if defender field is already full (3 cards)" in {
      val fieldState = mock[IDataManager]
      val player = mock[IPlayer]

      val existingDefenders = (1 to 3).map { i =>
        val c = mock[ICard]
        when(c.valueToInt).thenReturn(i)
        c
      }.toList

      val hand = new HandCardsQueue(Nil)

      when(fieldState.getPlayerDefenders(player)).thenReturn(existingDefenders)
      when(fieldState.getPlayerGoalkeeper(player)).thenReturn(Some(mock[ICard]))

      val strategy = new RefillField
      strategy.refill(fieldState, player, hand)

      verify(fieldState, never()).setPlayerField(any(), any())
      verify(fieldState, never()).setPlayerGoalkeeper(any(), any())
      verify(fieldState, never()).setPlayerDefenders(any(), any())
    }

    "not refill if no cards are needed" in {
      val fieldState = mock[IDataManager]
      val player = mock[IPlayer]

      when(fieldState.getPlayerDefenders(player)).thenReturn(List.fill(3)(mock[ICard]))
      when(fieldState.getPlayerGoalkeeper(player)).thenReturn(Some(mock[ICard]))

      val hand = new HandCardsQueue(Nil)

      val strategy = new RefillField
      strategy.refill(fieldState, player, hand)

      hand.getHandSize shouldBe 0
      verify(fieldState, never()).setPlayerField(any(), any())
    }
  }
}
