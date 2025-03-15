package model.playingFiledComponent.strategy.boostStrategy.base

import model.cardComponent.ICard
import model.cardComponent.base.types.{BoostedCard, RegularCard}
import model.playerComponent.playerAction.*
import model.playerComponent.playerRole.{IRolesManager, RolesManager}
import model.playingFiledComponent.IPlayingField
import model.playingFiledComponent.manager.IDataManager
import model.playingFiledComponent.manager.base.DataManager
import model.playingFiledComponent.strategy.boostStrategy.IBoostStrategy

class DefenderBoostStrategy(index: Int) extends IBoostStrategy {

  override def boost(playingField: IPlayingField): Boolean = {
    lazy val data: IDataManager = playingField.getDataManager
    lazy val roles: IRolesManager = playingField.getRoles
    var attackersDefenderField = data.getPlayerDefenders(roles.attacker)

    println(s"[DEBUG] Initial defender field for attacker: $attackersDefenderField")

    if (index < 0 || index >= attackersDefenderField.size) {
      println(s"[DEBUG] Index $index out of bounds, exiting boost method.")
      return false
    }

    val attackerBeforeAction = roles.attacker

    // Check if the attacker has any Boost actions left
    attackerBeforeAction.actionStates.get(PlayerActionPolicies.Boost) match {
      case Some(OutOfActions) =>
        println(s"[DEBUG] ${attackerBeforeAction.name} has no Boost actions left. Boosting is prevented.")
        return false
      case Some(CanPerformAction(remainingUses)) if remainingUses <= 0 =>
        println(s"[DEBUG] ${attackerBeforeAction.name} has no Boost actions left. Boosting is prevented.")
        return false
      case _ => // Continue boosting
    }

    val originalCard = attackersDefenderField(index)
    println(s"[DEBUG] Original card before boost: $originalCard")

    val boostedCard = originalCard.boost()
    println(s"[DEBUG] Boosted card: $boostedCard")

    attackersDefenderField = attackersDefenderField.updated(index, boostedCard)
    println(s"[DEBUG] Updated defender field for attacker: $attackersDefenderField")

    data.setPlayerDefenders(roles.attacker, attackersDefenderField)

    // Perform the action and update the attacker's state
    val attackerAfterAction = attackerBeforeAction.performAction(PlayerActionPolicies.Boost)

    // Debug print for remaining uses of the Boost action
    attackerAfterAction.actionStates.get(PlayerActionPolicies.Boost) match {
      case Some(CanPerformAction(remainingUses)) =>
        println(s"[DEBUG] Remaining Boost uses for ${attackerAfterAction.name}: $remainingUses")
      case Some(OutOfActions) =>
        println(s"[DEBUG] ${attackerAfterAction.name} has no Boost actions left.")
      case _ =>
        println(s"[DEBUG] Unexpected state for Boost action.")
    }

    roles.setRoles(attackerAfterAction, roles.defender) // Update only the attacker

    playingField.notifyObservers()
    println(s"[DEBUG] Observers notified of the change.")
    true
  }
}
