package de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.gameComponent.playingFiledComponent.manager.{IDataManager, IRolesManager, RolesManager}

class RevertCard {
  def revertCard(playingField: IPlayingField, card: ICard): ICard = {
    lazy val data: IDataManager = playingField.getDataManager
    val revertedCard = card match {
      case boosted: BoostedCard => boosted.revertBoost()
      case regular => regular
    }

    val attackerField = data.getPlayerDefenders(playingField.getRoles.attacker)
    val defenderField = data.getPlayerDefenders(playingField.getRoles.defender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    data.setPlayerDefenders(playingField.getRoles.attacker, updatedAttackerField)
    data.setPlayerDefenders(playingField.getRoles.defender, updatedDefenderField)

    playingField.notifyObservers()

    revertedCard
  }

}
