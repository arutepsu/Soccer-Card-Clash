package model.playingFiledComponent.strategy.boostStrategy.base

import controller.{Events, NoBoostsEvent}
import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.*
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.{DataManager, IDataManager}
import model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class DefenderBoostStrategy(index: Int) extends IBoostStrategy {

  override def boost(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    var attackersDefenderField = data.getPlayerDefenders(roles.attacker)
    

    if (index < 0 || index >= attackersDefenderField.size) {
      return false
    }

    val attackerBeforeAction = roles.attacker
    
    attackerBeforeAction.actionStates.get(PlayerActionPolicies.Boost) match {
      case Some(OutOfActions) =>
        playingField.notifyObservers(NoBoostsEvent(attackerBeforeAction))
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
        playingField.notifyObservers(NoBoostsEvent(attackerBeforeAction))
        return false
      case _ =>
    }

    val originalCard = attackersDefenderField(index)

    val boostedCard = originalCard.boost()

    attackersDefenderField = attackersDefenderField.updated(index, boostedCard)

    data.setPlayerDefenders(roles.attacker, attackersDefenderField)
    
    val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.Boost)
    
    attackerAfterAction.actionStates.get(PlayerActionPolicies.Boost) match {
      case Some(CanPerformAction(remainingUses)) =>
      case Some(OutOfActions) => playingField.notifyObservers(NoBoostsEvent(attackerBeforeAction))
      case _ =>
    }

    roles.setRoles(attackerAfterAction, roles.defender)

    playingField.notifyObservers()
    true
  }
}
