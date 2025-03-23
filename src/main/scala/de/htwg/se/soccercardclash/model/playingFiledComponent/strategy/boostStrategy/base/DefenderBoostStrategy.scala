package de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.base

import de.htwg.se.soccercardclash.controller.{Events, NoBoostsEvent}
import de.htwg.se.soccercardclash.model.cardComponent.ICard
import de.htwg.se.soccercardclash.model.cardComponent.base.types.{BoostedCard, RegularCard}
import de.htwg.se.soccercardclash.model.playerComponent.playerAction.*
import de.htwg.se.soccercardclash.model.playingFiledComponent.IPlayingField
import de.htwg.se.soccercardclash.model.playingFiledComponent.manager.{DataManager, IDataManager, IPlayerActionManager, IRolesManager, RolesManager}
import de.htwg.se.soccercardclash.model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class DefenderBoostStrategy(index: Int, playerActionService: IPlayerActionManager) extends IBoostStrategy {

  override def boost(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    var attackersDefenderField = data.getPlayerDefenders(roles.attacker)
    

    if (index < 0 || index >= attackersDefenderField.size) {
      return false
    }

    val attackerBeforeAction = roles.attacker
    
    if (!playerActionService.canPerform(attackerBeforeAction, PlayerActionPolicies.Boost)) {
      playingField.notifyObservers(NoBoostsEvent(attackerBeforeAction))
      return false
    }

    val originalCard = attackersDefenderField(index)

    val boostedCard = originalCard.boost()

    attackersDefenderField = attackersDefenderField.updated(index, boostedCard)

    data.setPlayerDefenders(roles.attacker, attackersDefenderField)
    
    val attackerAfterAction = playerActionService.performAction(attackerBeforeAction, PlayerActionPolicies.Boost)

    roles.setRoles(attackerAfterAction, roles.defender)

    playingField.notifyObservers()
    true
  }
}
