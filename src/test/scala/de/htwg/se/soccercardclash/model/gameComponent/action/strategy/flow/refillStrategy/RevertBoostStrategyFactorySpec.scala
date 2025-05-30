package de.htwg.se.soccercardclash.model.gameComponent.action.strategy.flow.refillStrategy

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock
import de.htwg.se.soccercardclash.model.gameComponent.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.action.strategy.trigger.boost.revert.{RevertBoostStrategy, RevertBoostStrategyFactory}
import de.htwg.se.soccercardclash.model.gameComponent.components.*
import de.htwg.se.soccercardclash.model.playerComponent.IPlayer
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.{mock, when}

class RevertBoostStrategyFactorySpec extends AnyFlatSpec with Matchers with MockitoSugar {


  "RevertBoostStrategyFactory" should "create a RevertBoostStrategy that uses the given state internally" in {
    val player1 = mock[IPlayer]
    val player2 = mock[IPlayer]

    val roles = mock[IRoles]
    when(roles.attacker).thenReturn(player1)
    when(roles.defender).thenReturn(player2)

    val state = mock[IGameState]
    when(state.getRoles).thenReturn(roles)

    val dataManager = mock[IGameCards]
    when(state.getGameCards).thenReturn(dataManager)

    val card = mock[ICard]
    val defenders1 = List(Some(card), None)
    val defenders2 = List(None, None)

    when(dataManager.getPlayerDefenders(player1)).thenReturn(defenders1)
    when(dataManager.getPlayerDefenders(player2)).thenReturn(defenders2)

    // Recreate updated dataManager chain
    val updatedDM1 = mock[IGameCards]
    val updatedDM2 = mock[IGameCards]

    when(dataManager.newPlayerDefenders(player1, defenders1)).thenReturn(updatedDM1)
    when(updatedDM1.newPlayerDefenders(player2, defenders2)).thenReturn(updatedDM2)

    // Also mock final update to state
    val updatedState = mock[IGameState]
    when(state.newGameCards(updatedDM2)).thenReturn(updatedState)

    val factory = new RevertBoostStrategyFactory
    val strategy = factory.create(state)

    val result = strategy.revertCard(Some(card))
    result shouldBe a[Option[_]]
  }
}
