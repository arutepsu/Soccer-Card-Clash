package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.components.IGameCards
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base.FieldRefillStrategy
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{eq => eqTo, any}

class RefillFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {
  class TestableFieldRefillStrategy extends FieldRefillStrategy {
    def testDetermineFieldCards(hand: IHandCardsQueue, defenders: Int, goalie: Int): (List[ICard], IHandCardsQueue) =
      super.determineFieldCards(hand, defenders, goalie)
  }
  "A RefillField" should {
    "refill defenders and goalkeeper when all are empty" in {
      val player = mock[IPlayer]
      val gameCards = mock[IGameCards]
      val hand = mock[IHandCardsQueue]
      val updatedHand = mock[IHandCardsQueue]

      val card1 = mock[ICard]
      val card2 = mock[ICard]
      val card3 = mock[ICard]
      val card4 = mock[ICard]

      when(card1.valueToInt).thenReturn(1)
      when(card2.valueToInt).thenReturn(2)
      when(card3.valueToInt).thenReturn(3)
      when(card4.valueToInt).thenReturn(4)

      val drawn = List(card1, card2, card3, card4)

      when(gameCards.getPlayerDefenders(player)).thenReturn(List(None, None, None))
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(None)
      when(hand.splitAtEnd(4)).thenReturn((drawn, updatedHand))

      val updatedCards = mock[IGameCards]
      val withGoalkeeper = mock[IGameCards]
      val withHand = mock[IGameCards]

      when(gameCards.newPlayerDefenders(eqTo(player), any())).thenReturn(updatedCards)
      when(updatedCards.newPlayerGoalkeeper(eqTo(player), any())).thenReturn(withGoalkeeper)
      when(withGoalkeeper.newPlayerHand(player, updatedHand)).thenReturn(withHand)

      val strategy = new FieldRefillStrategy
      val result = strategy.refill(gameCards, player, hand)

      result shouldBe withHand
    }

    "not refill if defenders are not all empty or goalkeeper is present" in {
      val player = mock[IPlayer]
      val gameCards = mock[IGameCards]
      val hand = mock[IHandCardsQueue]

      when(gameCards.getPlayerDefenders(player)).thenReturn(List(Some(mock[ICard]), None, None))
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(None)

      val strategy = new FieldRefillStrategy
      val result = strategy.refill(gameCards, player, hand)

      result shouldBe gameCards
    }
    "return 4 cards when both defender and goalkeeper are empty" in {
      val hand = mock[IHandCardsQueue]
      val updatedHand = mock[IHandCardsQueue]
      val cards = List(mock[ICard], mock[ICard], mock[ICard], mock[ICard])
      when(hand.splitAtEnd(4)).thenReturn((cards, updatedHand))

      val strategy = new TestableFieldRefillStrategy
      val (drawn, resultHand) = strategy.testDetermineFieldCards(hand, 0, 0)

      drawn shouldBe cards
      resultHand shouldBe updatedHand
    }
    "return 2 cards when one defender is present" in {
      val hand = mock[IHandCardsQueue]
      val updatedHand = mock[IHandCardsQueue]
      val cards = List(mock[ICard], mock[ICard])
      when(hand.splitAtEnd(2)).thenReturn((cards, updatedHand))

      val strategy = new TestableFieldRefillStrategy
      val (drawn, resultHand) = strategy.testDetermineFieldCards(hand, 1, 1)

      drawn shouldBe cards
      resultHand shouldBe updatedHand
    }

    "return 1 card when two defenders are present" in {
      val hand = mock[IHandCardsQueue]
      val updatedHand = mock[IHandCardsQueue]
      val cards = List(mock[ICard])
      when(hand.splitAtEnd(1)).thenReturn((cards, updatedHand))

      val strategy = new TestableFieldRefillStrategy
      val (drawn, resultHand) = strategy.testDetermineFieldCards(hand, 2, 1)

      drawn shouldBe cards
      resultHand shouldBe updatedHand
    }

    "return no cards when field is full (3 defenders)" in {
      val hand = mock[IHandCardsQueue]

      val strategy = new TestableFieldRefillStrategy
      val (drawn, resultHand) = strategy.testDetermineFieldCards(hand, 3, 1)

      drawn shouldBe empty
      resultHand shouldBe hand
    }
  }
}
