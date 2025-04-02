package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.refillStrategy.base

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.IDataManager
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.{HandCardsQueue, IHandCardsQueue}
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.refillStrategy.base.{RefillDefenderField, RefillField}

class RefillDefenderFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A RefillDefenderField" should {

    "completely refill when goalkeeper and defender field are empty" in {
      val fieldState = mock[IDataManager]
      val defender = mock[IPlayer]

      val cards = (1 to 4).map { i =>
        val c = mock[ICard]
        when(c.valueToInt).thenReturn(i)
        c
      }.toList

      val handQueue = new HandCardsQueue(cards)

      when(fieldState.getPlayerDefenders(defender)).thenReturn(List.empty)
      when(fieldState.getPlayerGoalkeeper(defender)).thenReturn(None)
      when(fieldState.getPlayerHand(defender)).thenReturn(handQueue)

      val strategy = new RefillDefenderField
      strategy.refill(fieldState, defender)

      val expectedGoalkeeper = cards.maxBy(_.valueToInt)
      val expectedDefenders = cards.filterNot(_ == expectedGoalkeeper)

      handQueue.size shouldBe 0

      verify(fieldState).setPlayerGoalkeeper(defender, Some(expectedGoalkeeper))
      verify(fieldState).setPlayerDefenders(defender, expectedDefenders)
    }

    "partially refill when defender field has less than 3 cards and goalkeeper exists" in {
      val fieldState = mock[IDataManager]
      val defender = mock[IPlayer]

      val existingDefenders = (1 to 2).map { i =>
        val c = mock[ICard]
        when(c.valueToInt).thenReturn(i)
        c
      }.toList

      val newCard = mock[ICard]
      when(newCard.valueToInt).thenReturn(10)

      val goalkeeper = mock[ICard]
      when(goalkeeper.valueToInt).thenReturn(5)

      val allCards = List(newCard) // only 1 card needed to refill to 3 defenders
      val handQueue = new HandCardsQueue(allCards)

      when(fieldState.getPlayerDefenders(defender)).thenReturn(existingDefenders)
      when(fieldState.getPlayerGoalkeeper(defender)).thenReturn(Some(goalkeeper))
      when(fieldState.getPlayerHand(defender)).thenReturn(handQueue)

      val strategy = new RefillDefenderField
      strategy.refill(fieldState, defender)

      val expectedGoalkeeper = newCard
      val expectedDefenders = existingDefenders :+ goalkeeper

      handQueue.size shouldBe 0

      verify(fieldState).setPlayerGoalkeeper(defender, Some(expectedGoalkeeper))
      verify(fieldState).setPlayerDefenders(defender, expectedDefenders)
    }


    "do nothing if defender field is full" in {
      val fieldState = mock[IDataManager]
      val defender = mock[IPlayer]

      val defenders = (1 to 3).map { i =>
        val c = mock[ICard]
        when(c.valueToInt).thenReturn(i)
        c
      }.toList

      when(fieldState.getPlayerDefenders(defender)).thenReturn(defenders)
      when(fieldState.getPlayerGoalkeeper(defender)).thenReturn(Some(mock[ICard]))

      val strategy = new RefillDefenderField
      strategy.refill(fieldState, defender)

      verify(fieldState, never()).setPlayerDefenders(any(), any())
      verify(fieldState, never()).setPlayerGoalkeeper(any(), any())
    }
  }
}
