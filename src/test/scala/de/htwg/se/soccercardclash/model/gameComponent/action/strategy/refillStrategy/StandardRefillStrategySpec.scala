package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.dataStructure.IHandCardsQueue
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.refillStrategy.base.{DefenderFieldRefillStrategy, FieldRefillStrategy, StandardRefillStrategy}
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class StandardRefillStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "A StandardRefillStrategy" should {

    "refill the defender field correctly" in {
      val mockGameCards = mock[IGameCards]
      val mockPlayer = mock[IPlayer]
      val mockRefilled = mock[IGameCards]

      val defenderField = mock[DefenderFieldRefillStrategy]
      val refill = new StandardRefillStrategy {
        override protected val defenderFieldRefill: DefenderFieldRefillStrategy = defenderField
      }

      when(defenderField.refill(mockGameCards, mockPlayer)).thenReturn(mockRefilled)

      val result = refill.refillDefenderField(mockGameCards, mockPlayer)
      result shouldBe mockRefilled
    }

    "refill the full field correctly" in {
      val mockGameCards = mock[IGameCards]
      val mockPlayer = mock[IPlayer]
      val mockHand = mock[IHandCardsQueue]
      val mockRefilled = mock[IGameCards]

      val field = mock[FieldRefillStrategy]
      val refill = new StandardRefillStrategy {
        override protected val fieldRefill: FieldRefillStrategy = field
      }

      when(field.refill(mockGameCards, mockPlayer, mockHand)).thenReturn(mockRefilled)

      val result = refill.refillField(mockGameCards, mockPlayer, mockHand)
      result shouldBe mockRefilled
    }
  }
}