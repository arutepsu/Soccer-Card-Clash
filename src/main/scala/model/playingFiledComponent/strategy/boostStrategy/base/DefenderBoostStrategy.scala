package model.playingFiledComponent.strategy.boostStrategy.base

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class DefenderBoostStrategy(index: Int) extends IBoostStrategy {
  
  override def boost(playingField: IPlayingField): Unit = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    var attackersDefenderField = data.getPlayerDefenders(roles.attacker)

    if (index < 0 || index >= attackersDefenderField.size) {
      return
    }

    val originalCard = attackersDefenderField(index)
    val boostedCard = originalCard.boost()

    attackersDefenderField = attackersDefenderField.updated(index, boostedCard)
    data.setPlayerDefenders(roles.attacker, attackersDefenderField)

    roles.attacker.performAction(PlayerActionPolicies.Boost)
    playingField.notifyObservers()
  }
}