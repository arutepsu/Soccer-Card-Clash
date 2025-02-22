package model.playingFiledComponent.strategy.boostStrategy.base

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.PlayerActionPolicies
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class GoalkeeperBoostStrategy extends IBoostStrategy {
  
  override def boost(playingField: IPlayingField): Unit = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackersGoalkeeperOpt = data.getPlayerGoalkeeper(roles.attacker)

    attackersGoalkeeperOpt match {
      case Some(goalkeeper) =>
        goalkeeper.boost()
        data.setGoalkeeperForAttacker(goalkeeper)
        roles.attacker.performAction(PlayerActionPolicies.Boost)
        playingField.notifyObservers()
      case None =>
    }
  }
}