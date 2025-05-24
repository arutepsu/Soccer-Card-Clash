package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IGameCards, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.{GameActionEvent, StateEvent}

class RevertCard {
  def revertCard(playingField: IGameState, card: Option[ICard]): Option[ICard] = {
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    var dataManager = playingField.getGameCards

    val revertedCard: Option[ICard] = card.map {
      case boosted: BoostedCard => boosted.revertBoost()
      case other                => other
    }

    val attackerField = dataManager.getPlayerDefenders(attacker)
    val defenderField = dataManager.getPlayerDefenders(defender)

    val updatedAttackerField = attackerField.map {
      case c if c == card => revertedCard
      case other          => other
    }

    val updatedDefenderField = defenderField.map {
      case c if c == card => revertedCard
      case other          => other
    }

    dataManager = dataManager
      .newPlayerDefenders(attacker, updatedAttackerField)
      .newPlayerDefenders(defender, updatedDefenderField)

    val updatedState = playingField.newGameCards(dataManager)
//    updatedState.notifyObservers(StateEvent.CardReverted(revertedCard, attacker))

    revertedCard
  }
}
