package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{DataManager, IDataManager, IPlayerActionManager, IRolesManager, RolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy
import de.htwg.se.soccercardclash.util.{Events, NoBoostsEvent}

class GoalkeeperBoostStrategy(
                               playerActionService: IPlayerActionManager
                             ) extends IBoostStrategy {

  override def boost(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    val attackersGoalkeeperOpt = data.getPlayerGoalkeeper(roles.attacker)

    attackersGoalkeeperOpt match {
      case Some(goalkeeper) =>
        val boostedGoalkeeper = goalkeeper.boost()
        data.setGoalkeeperForAttacker(boostedGoalkeeper)

        val attackerBeforeAction = roles.attacker
        
        if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.Boost)) {
          playingField.notifyObservers(NoBoostsEvent(attackerBeforeAction))
          return false
        }
        
        val attackerAfterAction =
          playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.Boost)

        roles.setRoles(attackerAfterAction, roles.defender)

        playingField.notifyObservers()
        true
      case None => false
    }
  }
}
