package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy

import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.base.*
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar

class StandardRefillStrategySpec extends AnyFlatSpec with Matchers with MockitoSugar {

  "StandardRefillStrategy" should "delegate to defenderRefill.refill in refillDefenderField" in {
    val defenderRefill = mock[IDefenderFieldRefillStrategy]
    val fieldRefill = mock[IFieldRefillStrategy]
    val strategy = new StandardRefillStrategy(defenderRefill, fieldRefill)

    val gameCards = mock[IGameCards]
    val defender = mock[IPlayer]
    val resultCards = mock[IGameCards]


    when(defenderRefill.refill(gameCards, defender)).thenReturn(resultCards)

    val result = strategy.refillDefenderField(gameCards, defender)
    result shouldBe resultCards

    verify(defenderRefill).refill(gameCards, defender)
    verifyNoInteractions(fieldRefill)
  }

  it should "delegate to fieldRefill.refill in refillField" in {
    val defenderRefill = mock[IDefenderFieldRefillStrategy]
    val fieldRefill = mock[IFieldRefillStrategy]
    val strategy = new StandardRefillStrategy(defenderRefill, fieldRefill)

    val gameCards = mock[IGameCards]
    val player = mock[IPlayer]
    val resultCards = mock[IGameCards]
    val hand = mock[IHandCardsQueue]


    when(fieldRefill.refill(gameCards, player, hand)).thenReturn(resultCards)

    val result = strategy.refillField(gameCards, player, hand)
    result shouldBe resultCards

    verify(fieldRefill).refill(gameCards, player, hand)
    verifyNoInteractions(defenderRefill)
  }
}