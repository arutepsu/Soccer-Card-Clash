package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.BoostedCard
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.boost.revert.{RevertBoostStrategy, RevertCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.components.IGameCards
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

class RevertBoostStrategySpec extends AnyWordSpec with Matchers with MockitoSugar {

  "RevertBoostStrategy" should {
    "revert a boosted card using RevertCard" in {
      val playingField = mock[IGameState]
      val roles = mock[de.htwg.se.soccercardclash.model.gameComponent.state.components.IRoles]
      val gameCards = mock[IGameCards]
      val attacker = mock[IPlayer]
      val defender = mock[IPlayer]
      val boostedCard = mock[BoostedCard]
      val revertedCard = mock[ICard]

      when(playingField.getRoles).thenReturn(roles)
      when(roles.attacker).thenReturn(attacker)
      when(roles.defender).thenReturn(defender)
      when(playingField.getGameCards).thenReturn(gameCards)

      when(boostedCard.revertBoost()).thenReturn(revertedCard)

      when(gameCards.getPlayerDefenders(attacker)).thenReturn(List(Some(boostedCard)))
      when(gameCards.getPlayerDefenders(defender)).thenReturn(List.empty)

      val updatedGameCards = mock[IGameCards]
      val finalGameCards = mock[IGameCards]
      val finalState = mock[IGameState]

      when(gameCards.newPlayerDefenders(eqTo(attacker), any())).thenReturn(updatedGameCards)
      when(updatedGameCards.newPlayerDefenders(eqTo(defender), any())).thenReturn(finalGameCards)
      when(playingField.newGameCards(finalGameCards)).thenReturn(finalState)

      val strategy = new RevertBoostStrategy(playingField)
      val result = strategy.revertCard(Some(boostedCard))

      result shouldBe Some(revertedCard)
    }
  }
}
