package de.htwg.se.soccercardclash.model.gameComponent.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.IRefillStrategy
class GameCardsFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {
  "A GameCardsFactory" should {
    "create empty GameCards with attacker and defender" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val mockHandCardsFactory = mock[IHandCardsFactory]
      val mockFieldCardsFactory = mock[IFieldCardsFactory]
      val mockRefillStrategy = mock[IRefillStrategy]
      val mockHandCards = mock[IHandCards]
      val mockFieldCards = mock[IFieldCards]
      val handQueue1 = mock[IHandCardsQueue]
      val handQueue2 = mock[IHandCardsQueue]

      when(mockHandCardsFactory.empty).thenReturn(mockHandCards)
      when(mockHandCards.getPlayerHand(player1)).thenReturn(handQueue1)
      when(mockHandCards.getPlayerHand(player2)).thenReturn(handQueue2)

      when(mockFieldCardsFactory.create(any(), any(), any())).thenReturn(mockFieldCards)
      when(mockFieldCards.getPlayerDefenders(player1)).thenReturn(List(None, None, None))
      when(mockFieldCards.getPlayerDefenders(player2)).thenReturn(List(None, None, None))
      when(mockFieldCards.getPlayerGoalkeeper(player1)).thenReturn(None)
      when(mockFieldCards.getPlayerGoalkeeper(player2)).thenReturn(None)

      val factory = new GameCardsFactory(mockHandCardsFactory, mockFieldCardsFactory, mockRefillStrategy)
      val result = factory.create(player1, player2)

      result.getPlayerHand(player1) shouldBe handQueue1
      result.getPlayerDefenders(player1) shouldBe List(None, None, None)
    }
    "create GameCards from data correctly" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val handCard1 = mock[ICard]
      val handCard2 = mock[ICard]
      val defCard1 = Some(mock[ICard])
      val defCard2 = Some(mock[ICard])
      val goalkeeper = Some(mock[ICard])

      val mockHandCardsFactory = mock[IHandCardsFactory]
      val mockFieldCardsFactory = mock[IFieldCardsFactory]
      val mockRefillStrategy = mock[IRefillStrategy]
      val mockHandCards = mock[IHandCards]
      val mockFieldCards = mock[IFieldCards]
      val mockHandQueue1 = mock[IHandCardsQueue]
      val mockHandQueue2 = mock[IHandCardsQueue]

      when(mockHandCardsFactory.create(player1, List(handCard1), player2, List(handCard2)))
        .thenReturn(mockHandCards)

      when(mockHandCards.getPlayerHand(player1)).thenReturn(mockHandQueue1)
      when(mockHandCards.getPlayerHand(player2)).thenReturn(mockHandQueue2)

      when(mockFieldCardsFactory.create(any(), any(), any())).thenReturn(mockFieldCards)
      when(mockRefillStrategy.refillField(any(), any(), any()))
        .thenAnswer(i => i.getArgument(0).asInstanceOf[IGameCards])

      val factory = new GameCardsFactory(mockHandCardsFactory, mockFieldCardsFactory, mockRefillStrategy)

      val result = factory.createFromData(
        attacker = player1,
        attackerHand = List(handCard1),
        defender = player2,
        defenderHand = List(handCard2),
        attackerDefenders = List(defCard1, None),
        defenderDefenders = List(defCard2, None),
        attackerGoalkeeper = goalkeeper,
        defenderGoalkeeper = goalkeeper
      )

      result.getPlayerHand(player1) shouldBe mockHandQueue1
      result.getPlayerHand(player2) shouldBe mockHandQueue2
    }
  }
}
