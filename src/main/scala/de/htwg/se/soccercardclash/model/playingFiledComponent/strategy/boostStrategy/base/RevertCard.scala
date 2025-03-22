package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.PlayerActionPolicies
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{IDataManager, IRolesManager, RolesManager}

class RevertCard {
  def revertCard(playingField: IPlayingField, card: ICard): ICard = {
    lazy val data: IDataManager = playingField.getDataManager
    val revertedCard = card match {
      case boosted: BoostedCard => boosted.revertBoost()
      case regular => regular
    }

    val attackerField = data.getPlayerDefenders(playingField.getAttacker)
    val defenderField = data.getPlayerDefenders(playingField.getDefender)

    val updatedAttackerField = attackerField.map(c => if (c == card) revertedCard else c)
    val updatedDefenderField = defenderField.map(c => if (c == card) revertedCard else c)

    data.setPlayerDefenders(playingField.getAttacker, updatedAttackerField)
    data.setPlayerDefenders(playingField.getDefender, updatedDefenderField)

    playingField.notifyObservers()

    revertedCard
  }

}
