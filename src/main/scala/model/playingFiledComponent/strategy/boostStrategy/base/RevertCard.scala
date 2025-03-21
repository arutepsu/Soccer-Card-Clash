package model.playingFiledComponent.strategy.boostStrategy.base

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{IDataManager, IRolesManager, RolesManager}

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
