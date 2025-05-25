package de.htwg.se.soccercardclash.model.gameComponent.state.components

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.state.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
class GameCardsFactorySpec extends AnyWordSpec with Matchers with MockitoSugar {
  "A GameCardsFactory" should {
    "create empty GameCards with attacker and defender" in {
      val player1 = mock[IPlayer]
      val player2 = mock[IPlayer]

      val mockHandCardsFactory = mock[IHandCardsFactory]
      val mockFieldCardsFactory = mock[IFieldCardsFactory]

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

      val factory = new GameCardsFactory(mockHandCardsFactory, mockFieldCardsFactory)
      val result = factory.create(player1, player2)

      result.getPlayerHand(player1) shouldBe handQueue1
      result.getPlayerDefenders(player1) shouldBe List(None, None, None)
    }
    //
  }
}
