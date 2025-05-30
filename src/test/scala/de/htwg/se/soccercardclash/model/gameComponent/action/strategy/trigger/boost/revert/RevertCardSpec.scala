package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert

import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.components.{IGameCards, IRoles}
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RevertCardSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RevertCard" should {
    "revert a boosted card in attacker and defender fields" in {
      val state = mock[IGameState]
      val gameCards = mock[IGameCards]
      val roles = mock[IRoles]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]

      val boostedCard = mock[BoostedCard]
      val revertedCard = mock[ICard]

      // Defender fields have a boosted card to revert
      val attackerField = List(Some(boostedCard), None, None)
      val defenderField = List(None, Some(boostedCard), None)

      val updatedGameCards1 = mock[IGameCards]
      val updatedGameCards2 = mock[IGameCards]
      val updatedState = mock[IGameState]

      // Mocks
      when(state.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(attacker)
      when(roles.defender).thenReturn(defender)
      when(state.getGameCards).thenReturn(gameCards)

      when(gameCards.getPlayerDefenders(attacker)).thenReturn(attackerField)
      when(gameCards.getPlayerDefenders(defender)).thenReturn(defenderField)

      when(boostedCard.revertBoost()).thenReturn(revertedCard)

      when(gameCards.newPlayerDefenders(eqTo(attacker), any())).thenReturn(updatedGameCards1)
      when(updatedGameCards1.newPlayerDefenders(eqTo(defender), any())).thenReturn(updatedGameCards2)

      when(state.newGameCards(updatedGameCards2)).thenReturn(updatedState)

      val result = new RevertCard().revertCard(state, Some(boostedCard))

      result shouldBe Some(revertedCard)
      verify(gameCards).newPlayerDefenders(eqTo(attacker), any())
      verify(updatedGameCards1).newPlayerDefenders(eqTo(defender), any())
      verify(state).newGameCards(updatedGameCards2)
    }
  }
}
