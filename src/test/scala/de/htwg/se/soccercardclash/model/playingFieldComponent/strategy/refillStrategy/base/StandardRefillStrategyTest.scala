package de.htwg.se.soccercardclash.model.playingFieldComponent.strategy.refillStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.IRefillStrategy
import de.htwg.se.soccercardclash.model.gameComponent.state.strategy.refillStrategy.base.{RefillDefenderField, RefillField, StandardRefillStrategy}
import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.*
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

import scala.collection.mutable

class StandardRefillStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  // Mocks
  val mockFieldState: IGameCards = mock[IGameCards]
  val mockPlayer: IPlayer = mock[IPlayer]
  val mockCard: ICard = mock[ICard]
  val mockHand: IHandCardsQueue = new HandCardsQueue(List.fill(4)(mock[ICard]))
  val mockDefenderField: List[ICard] = List(mock[ICard], mock[ICard], mock[ICard])
  val mockGoalkeeper: Option[ICard] = Some(mock[ICard])

  "StandardRefillStrategy" should "refill defender field when refillDefenderField is called" in {
    val defenderFieldRefillMock = mock[RefillDefenderField]
    when(defenderFieldRefillMock.refill(mockFieldState, mockPlayer)).thenReturn(mockHand)

    val refillStrategy = new StandardRefillStrategy {
      override val defenderFieldRefill: RefillDefenderField = defenderFieldRefillMock
    }

    refillStrategy.refillDefenderField(mockFieldState, mockPlayer)

    verify(defenderFieldRefillMock).refill(mockFieldState, mockPlayer)
  }

  it should "refill field when refillField is called" in {
    val fieldRefillMock = mock[RefillField]
    when(fieldRefillMock.refill(mockFieldState, mockPlayer, mockHand)).thenReturn(mockHand)

    val refillStrategy = new StandardRefillStrategy {
      override val fieldRefill: RefillField = fieldRefillMock
    }

    refillStrategy.refillField(mockFieldState, mockPlayer, mockHand)

    verify(fieldRefillMock).refill(mockFieldState, mockPlayer, mockHand)
  }

}