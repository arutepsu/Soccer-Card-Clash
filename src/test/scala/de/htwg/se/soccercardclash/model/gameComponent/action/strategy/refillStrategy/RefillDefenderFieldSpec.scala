package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.RefillDefenderField
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RefillDefenderFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RefillDefenderField" should {

    "refill completely when all defenders and goalkeeper are empty" in {
      val player = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val cards = List.fill(4)(mock[ICard])
      val updatedHand = mock[IHandCardsQueue]

      val gameCards = mock[IGameCards]
      val updatedGC1 = mock[IGameCards]
      val updatedGC2 = mock[IGameCards]
      val updatedGC3 = mock[IGameCards]

      when(gameCards.getPlayerDefenders(player)).thenReturn(List(None, None, None))
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(None)
      when(gameCards.getPlayerHand(player)).thenReturn(hand)
      when(hand.splitAtEnd(4)).thenReturn((cards, updatedHand))

      val best = cards.head
      when(best.valueToInt).thenReturn(100)
      cards.tail.foreach(card => when(card.valueToInt).thenReturn(10))

      val expectedDefenders = cards.filterNot(_ == best).map(Some(_)).padTo(3, None)

      when(gameCards.newPlayerGoalkeeper(eqTo(player), eqTo(Some(best)))).thenReturn(updatedGC1)
      when(updatedGC1.newPlayerDefenders(eqTo(player), eqTo(expectedDefenders))).thenReturn(updatedGC2)
      when(updatedGC2.newPlayerHand(eqTo(player), eqTo(updatedHand))).thenReturn(updatedGC3)

      val strategy = new RefillDefenderField()
      val result = strategy.refill(gameCards, player)

      result shouldBe updatedGC3
    }

    "refill partially when some defender slots are empty and goalkeeper is present" in {
      val player = mock[IPlayer]
      val hand = mock[IHandCardsQueue]
      val defenderCard = mock[ICard]
      val newCard = mock[ICard]
      val updatedHand = mock[IHandCardsQueue]
      val goalkeeper = mock[ICard]

      when(newCard.valueToInt).thenReturn(15)
      when(goalkeeper.valueToInt).thenReturn(10)

      val gameCards = mock[IGameCards]
      val updatedGC1 = mock[IGameCards]
      val updatedGC2 = mock[IGameCards]
      val updatedGC3 = mock[IGameCards]

      val currentDefenders = List(Some(defenderCard), None, None)

      when(gameCards.getPlayerDefenders(player)).thenReturn(currentDefenders)
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(Some(goalkeeper))
      when(gameCards.getPlayerHand(player)).thenReturn(hand)

      when(hand.splitAtEnd(2)).thenReturn((List(newCard, mock[ICard]), updatedHand))

      val adjustedDefenders = List(Some(defenderCard), Some(newCard), Some(goalkeeper))

      when(gameCards.newPlayerGoalkeeper(eqTo(player), eqTo(Some(newCard)))).thenReturn(updatedGC1)
      when(updatedGC1.newPlayerDefenders(eqTo(player), any())).thenReturn(updatedGC2)
      when(updatedGC2.newPlayerHand(eqTo(player), eqTo(updatedHand))).thenReturn(updatedGC3)

      val strategy = new RefillDefenderField()
      val result = strategy.refill(gameCards, player)

      result shouldBe updatedGC3
    }

    "return unchanged GameCards if no refill is needed" in {
      val player = mock[IPlayer]
      val gameCards = mock[IGameCards]

      val defenders = List(Some(mock[ICard]), Some(mock[ICard]), Some(mock[ICard]))
      val goalkeeper = Some(mock[ICard])

      when(gameCards.getPlayerDefenders(player)).thenReturn(defenders)
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(goalkeeper)

      val strategy = new RefillDefenderField()
      strategy.refill(gameCards, player) shouldBe gameCards
    }
  }
}
