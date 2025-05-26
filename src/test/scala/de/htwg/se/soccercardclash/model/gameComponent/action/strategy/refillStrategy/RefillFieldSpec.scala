package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.RefillField
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.{eq => eqTo, any}

class RefillFieldSpec extends AnyWordSpec with Matchers with MockitoSugar {

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

      val strategy = new RefillField
      val result = strategy.refill(gameCards, player, hand)

      result shouldBe withHand
    }

    "not refill if defenders are not all empty or goalkeeper is present" in {
      val player = mock[IPlayer]
      val gameCards = mock[IGameCards]
      val hand = mock[IHandCardsQueue]

      when(gameCards.getPlayerDefenders(player)).thenReturn(List(Some(mock[ICard]), None, None))
      when(gameCards.getPlayerGoalkeeper(player)).thenReturn(None)

      val strategy = new RefillField
      val result = strategy.refill(gameCards, player, hand)

      result shouldBe gameCards
    }
  }
}
