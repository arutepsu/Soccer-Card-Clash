package de.htwg.se.soccercardclash.model.gameComponent.state.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.gameComponent.state.IGameState
import de.htwg.se.soccercardclash.model.gameComponent.state.components.{IDataManager, IRoles, Roles}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.util.Events

class RevertCard {
  def revertCard(playingField: IGameState, card: ICard): ICard = {
    val roles = playingField.getRoles
    val attacker = roles.attacker
    val defender = roles.defender

    var dataManager = playingField.getDataManager

    val revertedCard = card match {
      case boosted: BoostedCard => boosted.revertBoost()
      case regular => regular
    }

    val attackerField = dataManager.getPlayerDefenders(attacker)
    val defenderField = dataManager.getPlayerDefenders(defender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    dataManager = dataManager
      .setPlayerDefenders(attacker, updatedAttackerField)
      .setPlayerDefenders(defender, updatedDefenderField)

    val updatedState = playingField.withDataManager(dataManager)
    updatedState.notifyObservers(Events.CardReverted(revertedCard, attacker))

    revertedCard
  }
}
