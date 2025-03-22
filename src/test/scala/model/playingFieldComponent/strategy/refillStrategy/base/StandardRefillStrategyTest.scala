package model.playingFieldComponent.strategy.refillStrategy.base

import model.cardComponent.ICard
import model.playerComponent.IPlayer
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.strategy.refillStrategy.IRefillStrategy
import model.playingFiledComponent.strategy.refillStrategy.base.{RefillDefenderField, RefillField, StandardRefillStrategy}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*

import scala.collection.mutable

class StandardRefillStrategyTest extends AnyFlatSpec with Matchers with MockitoSugar {

  // Mocks
  val mockFieldState: IDataManager = mock[IDataManager]
  val mockPlayer: IPlayer = mock[IPlayer]
  val mockCard: ICard = mock[ICard]
  val mockHand: mutable.Queue[ICard] = mutable.Queue(mock[ICard], mock[ICard], mock[ICard], mock[ICard])
  val mockDefenderField: List[ICard] = List(mock[ICard], mock[ICard], mock[ICard])
  val mockGoalkeeper: Option[ICard] = Some(mock[ICard])

  "StandardRefillStrategy" should "refill defender field when refillDefenderField is called" in {
    // Mock the refill behavior of RefillDefenderField (void method)
    val defenderFieldRefillMock = mock[RefillDefenderField]
    doNothing().when(defenderFieldRefillMock).refill(mockFieldState, mockPlayer)  // Use doNothing for void methods

    // Create StandardRefillStrategy and inject the mock
    val refillStrategy = new StandardRefillStrategy {
      override val defenderFieldRefill: RefillDefenderField = defenderFieldRefillMock
    }

    refillStrategy.refillDefenderField(mockFieldState, mockPlayer)

    // Verify refillDefenderField method calls the refill method
    verify(defenderFieldRefillMock).refill(mockFieldState, mockPlayer)
  }

  it should "refill field when refillField is called" in {
    // Mock the refill behavior of RefillField (void method)
    val fieldRefillMock = mock[RefillField]
    doNothing().when(fieldRefillMock).refill(mockFieldState, mockPlayer, mockHand)  // Use doNothing for void methods

    // Create StandardRefillStrategy and inject the mock
    val refillStrategy = new StandardRefillStrategy {
      override val fieldRefill: RefillField = fieldRefillMock
    }

    refillStrategy.refillField(mockFieldState, mockPlayer, mockHand)

    // Verify refillField method calls the refill method
    verify(fieldRefillMock).refill(mockFieldState, mockPlayer, mockHand)
  }
}